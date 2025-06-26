package org.warriorcats.pawsOfTheForest.preys;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.ModelEngine;
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
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsPassives;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public class EventsPreys implements LoadingListener {

    public static final float COMMON_SPAWN_CHANCE = 0.15f;
    public static final float HIGHER_SPAWN_CHANCE = 0.05f;

    public static final int DEFAULT_FLEE_RADIUS = 6;
    public static final int DEFAULT_SPAWN_SCAN_DELAY_S = 10;

    private static final Map<UUID, BukkitTask> FLEEING_PREYS = new HashMap<>();

    // Handling spawn
    @Override
    public void load() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Function<Player, Location> getLocation = (player) -> {
                    Location loc = player.getLocation().clone().add(
                            (Math.random() - 0.5) * 20,
                            0,
                            (Math.random() - 0.5) * 20
                    );
                    loc.setY(player.getWorld().getHighestBlockYAt(loc) + 1);
                    return loc;
                };
                BiFunction<Location, Prey, Boolean> isSuitable = (location, prey) -> {
                    if (location.getBlock().isLiquid()) return prey.isAquatic();
                    if (location.clone().subtract(0, 1, 0).getBlock().isLiquid()) return prey.isAquatic();
                    return !prey.isAquatic();
                };
                for (Player player : Bukkit.getOnlinePlayers()) {
                    PlayerEntity playerEntity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
                    List<Prey> preys = new ArrayList<>();
                    double higherSpawnChance = !playerEntity.hasAbility(Skills.BLOOD_HUNTER) ?
                            HIGHER_SPAWN_CHANCE :
                            HIGHER_SPAWN_CHANCE * (1 + playerEntity.getAbilityTier(Skills.BLOOD_HUNTER) * EventsSkillsPassives.BLOOD_HUNTER_TIER_PERCENTAGE);
                    if (Math.random() < higherSpawnChance) {
                        preys = Prey.getAllHighers();
                    } else if (Math.random() < COMMON_SPAWN_CHANCE) {
                        preys = Prey.getAllCommons();
                    }
                    if (!preys.isEmpty()) {
                        Prey toSpawn = preys.get(new Random().nextInt(preys.size()));
                        Location locationToSpawn = getLocation.apply(player);
                        if (isSuitable.apply(locationToSpawn, toSpawn)) {
                            try {
                                MobsUtils.spawn(locationToSpawn, toSpawn.entityType().toLowerCase(), Math.random());
                            } catch (IllegalArgumentException ignored) {
                                // We let vanilla spawn working as well so no fallback here
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(PawsOfTheForest.getInstance(), 0, 20 * DEFAULT_SPAWN_SCAN_DELAY_S);
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
                            ticks += 20;
                            if (ticks >= Prey.fromEntity(nearbyLiving).get().fleeDurationSeconds() * 20) {
                                FLEEING_PREYS.remove(nearbyLiving.getUniqueId());
                                this.cancel();
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
                return player;
            }));
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.SKILL_POINTS_EARNED + event.getDroppedExp());
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.COINS_EARNED + prey.coins());
        }
    }
}
