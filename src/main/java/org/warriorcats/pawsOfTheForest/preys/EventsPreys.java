package org.warriorcats.pawsOfTheForest.preys;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.ModelEngine;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class EventsPreys implements LoadingListener {

    public static final float COMMON_SPAWN_CHANCE = 0.1f;

    public static final int DEFAULT_FLEE_RADIUS = 6;
    public static final int DEFAULT_FLEE_DURATION_S = 8;

    private static final Map<UUID, BukkitTask> FLEEING_PREYS = new HashMap<>();

    // Handling spawn
    @Override
    public void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (Math.random() < COMMON_SPAWN_CHANCE) {
                        Location loc = player.getLocation().clone().add(
                                (Math.random() - 0.5) * 20,
                                0,
                                (Math.random() - 0.5) * 20
                        );
                        loc.setY(player.getWorld().getHighestBlockYAt(loc) + 1);

                        if (loc.getBlock().isLiquid()) continue;
                        if (loc.clone().subtract(0, 1, 0).getBlock().isLiquid()) continue;

                        MobsUtils.spawn(loc, "mouse", Math.random());
                    }
                }
            }
        }.runTaskTimer(PawsOfTheForest.getInstance(), 0, 20 * 10);
    }

    // Handling flee behavior
    @EventHandler
    public void on(PlayerMoveEvent event) {
        for (Entity nearby : event.getPlayer().getNearbyEntities(DEFAULT_FLEE_RADIUS, DEFAULT_FLEE_RADIUS, DEFAULT_FLEE_RADIUS)) {
            if (nearby instanceof LivingEntity nearbyLiving && Prey.isPrey(nearbyLiving) &&
                    (!MobsUtils.isStealthFrom(event.getPlayer(), nearbyLiving) || FLEEING_PREYS.containsKey(nearbyLiving.getUniqueId()))) {
                Vector fleeVector = nearbyLiving.getLocation().toVector()
                        .subtract(event.getPlayer().getLocation().toVector()).normalize().multiply(0.35);

                nearbyLiving.setVelocity(fleeVector);

                Location loc = nearbyLiving.getLocation();
                Vector dir = fleeVector.clone().normalize();
                float yaw = (float) Math.toDegrees(Math.atan2(-dir.getX(), dir.getZ()));
                loc.setYaw(yaw);

                nearbyLiving.teleport(loc);

                ModeledEntity modeled = ModelEngine.getModeledEntity(nearbyLiving);
                if (modeled != null) {
                    modeled.getModels().values().forEach(model -> {
                        model.getAnimationHandler().playAnimation("run", 0, 0, 0, false);
                    });
                }
                if (!FLEEING_PREYS.containsKey(nearbyLiving.getUniqueId())) {
                    FLEEING_PREYS.put(nearbyLiving.getUniqueId(), new BukkitRunnable() {
                        int ticks = 0;

                        @Override
                        public void run() {
                            if (!nearbyLiving.isValid()) {
                                FLEEING_PREYS.remove(nearbyLiving.getUniqueId());
                                this.cancel();
                                return;
                            }

                            if (FLEEING_PREYS.containsKey(nearbyLiving.getUniqueId())) {
                                ticks += 20;
                                if (ticks >= DEFAULT_FLEE_DURATION_S * 20) {
                                    FLEEING_PREYS.remove(nearbyLiving.getUniqueId());
                                    this.cancel();
                                    return;
                                }
                            }
                        }
                    }.runTaskTimer(PawsOfTheForest.getInstance(), 0, 20));
                }
            }
        }
    }

    // Handling xp and coins giving when killing a prey
    @EventHandler
    public void on(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        Optional<Prey> existingPrey = Prey.fromEntity(event.getEntity());

        if (existingPrey.isPresent()) {
            Prey prey = existingPrey.get();
            HibernateUtils.withTransaction(((transaction, session) -> {
                PlayerEntity player = session.get(PlayerEntity.class, killer.getUniqueId());
                event.setDroppedExp((int) prey.xp());
                player.setXpPerks(player.getXpPerks() + prey.xp());
                player.setCoins(player.getCoins() + prey.coins());
            }));
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.XP_EARNED + event.getDroppedExp());
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.COINS_EARNED + prey.coins());
        }
    }
}
