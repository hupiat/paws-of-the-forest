package org.warriorcats.pawsOfTheForest.vitals;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class EventsVitals implements LoadingListener {

    public static final int BASE_MINECRAFT_FOOD_LEVEL_FOR_REGEN = 18;
    public static final long BASE_MINECRAFT_ACTIVITY_FREQUENCY_TICKS = (long) (20L * 1.5);

    public static final double BASE_REGEN_VALUE = 0.05;

    public static final double SPRINT_CONSUME_VALUE = 0.1;
    public static final double SWIM_CONSUME_VALUE = 0.01;
    public static final double JUMP_CONSUME_VALUE = 0.2;
    public static final double MINE_CONSUME_VALUE = 0.005;
    public static final double ATTACK_CONSUME_VALUE = 0.1;
    public static final double ATTACKED_CONSUME_VALUE = 0.1;

    // For hygiene & social
    public static final double BASE_CONSUME_VALUE = 0.005;

    private final Map<UUID, Double> distances = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastSocialActivities = new ConcurrentHashMap<>();

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
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        double distance = event.getFrom().distanceSquared(event.getTo());
        double total = distances.getOrDefault(player.getUniqueId(), 0.0);
        total += distance;

        if (total >= 4 && activityFrequency) {
            if (player.isSprinting()) {
                decreaseVitals(player, SPRINT_CONSUME_VALUE, SPRINT_CONSUME_VALUE, 0, 0);
            } else if (player.isSwimming()) {
                decreaseVitals(player, 0, SWIM_CONSUME_VALUE, 0, 0);
            } else if (player.isJumping()) {
                decreaseVitals(player, JUMP_CONSUME_VALUE, JUMP_CONSUME_VALUE, 0, 0);
            }
            activityFrequency = false;
        }

        if (distance == 0) {
            total = 0;
        }

        distances.put(player.getUniqueId(), total);
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
    public void onChat(AsyncPlayerChatEvent event) {
        lastSocialActivities.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        lastSocialActivities.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());
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
