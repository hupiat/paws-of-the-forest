package org.warriorcats.pawsOfTheForest.vitals;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventsVitals implements LoadingListener {

    public static final int BASE_MINECRAFT_FOOD_LEVEL_FOR_REGEN = 18;
    public static final int BASE_MINECRAFT_FOOT_STEP_BEFORE_CONSUMING = 4;
    public static final long BASE_MINECRAFT_ACTIVITY_FREQUENCY_TICKS = 80L;

    public static final double BASE_REGEN_VALUE = 0.05;
    public static final double SOCIAL_REGEN_VALUE = 0.1;
    public static final double DRINK_REGEN_VALUE = 0.3;

    public static final double BASE_CONSUME_VALUE = 0.005;
    public static final double SPRINT_CONSUME_VALUE = 0.1;
    public static final double SWIM_CONSUME_VALUE = 0.01;
    public static final double JUMP_CONSUME_VALUE = 0.02;
    public static final double MINE_CONSUME_VALUE = 0.005;
    public static final double ATTACK_CONSUME_VALUE = 0.1;
    public static final double ATTACKED_CONSUME_VALUE = 0.1;

    private final Map<UUID, Double> distances = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastSocialActivities = new ConcurrentHashMap<>();

    // Algorithm :
    // ----------------------------------------------------------
    // When the player walk, sprint, or swim, we are counting
    // the meters travelled. Then, when we reach a fixed value,
    // the vitals are decreased if the activity frequency is true.
    // ----------------------------------------------------------
    // This is how Minecraft's algorithm works under the hood :)
    private boolean activityFrequency = false;

    @Override
    public void load() {
        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            activityFrequency = !activityFrequency;
        }, 0L, BASE_MINECRAFT_ACTIVITY_FREQUENCY_TICKS);

        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            long now = System.currentTimeMillis();
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                long last = lastSocialActivities.getOrDefault(uuid, now);
                if (now - last >= 60000) {
                    decreaseVitals(player, 0, 0, BASE_CONSUME_VALUE, BASE_CONSUME_VALUE);
                }
            }
        }, 0L, 60L * 20L);

        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getFoodLevel() > BASE_MINECRAFT_FOOD_LEVEL_FOR_REGEN) {
                    increaseVitals(player, 0, BASE_REGEN_VALUE, 0, 0);
                }
            }
        }, 0L, 60L);
    }

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        increaseVitals(event.getPlayer(), 1, 1, 1, 1);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        double distance = event.getFrom().distanceSquared(event.getTo());
        double total = distances.getOrDefault(player.getUniqueId(), 0.0);
        total += distance;

        if (total >= BASE_MINECRAFT_FOOT_STEP_BEFORE_CONSUMING && activityFrequency) {
            if (player.isSprinting()) {
                decreaseVitals(player, SPRINT_CONSUME_VALUE, SPRINT_CONSUME_VALUE, 0, 0);
            } else if (player.isInWater()) {
                decreaseVitals(player, 0, SWIM_CONSUME_VALUE, 0, 0);
            } else {
                decreaseVitals(player, BASE_CONSUME_VALUE, BASE_CONSUME_VALUE, 0, 0);
            }
            activityFrequency = false;
        }

        if (distance == 0) {
            total = 0;
        }

        if (player.isInWaterOrRainOrBubbleColumn()) {
            increaseVitals(player, 1, 0, 1, 0);
        }

        distances.put(player.getUniqueId(), total);
    }

    @EventHandler
    public void on(PlayerJumpEvent event) {
        decreaseVitals(event.getPlayer(), JUMP_CONSUME_VALUE, JUMP_CONSUME_VALUE, 0, 0);
    }

    @EventHandler
    public void on(BlockBreakEvent event) {
        decreaseVitals(event.getPlayer(), MINE_CONSUME_VALUE, MINE_CONSUME_VALUE, MINE_CONSUME_VALUE, 0);
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        decreaseVitals(player, ATTACK_CONSUME_VALUE, ATTACK_CONSUME_VALUE, ATTACK_CONSUME_VALUE, 0);
    }

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        decreaseVitals(player, ATTACKED_CONSUME_VALUE, ATTACKED_CONSUME_VALUE, ATTACKED_CONSUME_VALUE, 0);
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        lastSocialActivities.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        increaseVitals(event.getPlayer(), 0, 0, 0, SOCIAL_REGEN_VALUE);
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        lastSocialActivities.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
        increaseVitals(event.getPlayer(), 0, 0, 0, SOCIAL_REGEN_VALUE);
    }

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        if (ItemsUtils.isDrinkable(event.getItem())) {
            increaseVitals(event.getPlayer(), DRINK_REGEN_VALUE, 0, 0, 0);
        }
    }

    private void decreaseVitals(Player player, double thirst, double energy, double hygiene, double social) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        HibernateUtils.withTransaction(((transaction, session) -> {
            entity.setThirst(entity.getThirst() - thirst);
            entity.setEnergy(entity.getEnergy() - energy);
            entity.setHygiene(entity.getHygiene() - hygiene);
            entity.setSocial(entity.getSocial() - social);
            return entity;
        }));
        HUD.updateInterface(player);
    }

    private void increaseVitals(Player player, double thirst, double energy, double hygiene, double social) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        HibernateUtils.withTransaction(((transaction, session) -> {
            entity.setThirst(entity.getThirst() + thirst);
            entity.setEnergy(entity.getEnergy() + energy);
            entity.setHygiene(entity.getHygiene() + hygiene);
            entity.setSocial(entity.getSocial() + social);
            return entity;
        }));
        HUD.updateInterface(player);
    }
}
