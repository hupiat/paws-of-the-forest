package org.warriorcats.pawsOfTheForest.illnesses;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventsIllnesses implements LoadingListener {

    public static final double BASE_INFECTION_RATE = 0.002;
    public static final double NEARBY_BASE_INFECTION_RATE = 0.0011;
    public static final int BASE_INFECTION_DISTANCE = 5;

    private final Map<UUID, List<Illnesses>> worsened = new ConcurrentHashMap<>();

    @Override
    public void load() {
        Bukkit.getScheduler().runTaskTimer(PawsOfTheForest.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
                for (Illnesses illness : Illnesses.values()) {
                    if (entity.hasIllness(illness)) {
                        IllnessEntity illnessEntity = entity.getIllness(illness);
                        if (illnessEntity.isWorsened()) {
                            if (illnessEntity.getIllness().isFatal()) {
                                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 1.0f, 0.5f);
                                player.setHealth(0);
                                player.sendMessage(MessagesConf.Illnesses.COLOR_FEEDBACK + MessagesConf.Illnesses.ILLNESS_WORSENED_DEATH + " " + illness);
                            } else {
                                if (worsened.containsKey(player.getUniqueId()) &&
                                        worsened.get(player.getUniqueId()).stream()
                                                .anyMatch(worsened -> worsened == illness)) {
                                    continue;
                                }
                                worsened.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>());
                                addPotionEffects(player, illness, illnessEntity.getAmplifier());
                                worsened.get(player.getUniqueId()).add(illness);
                                player.sendMessage(MessagesConf.Illnesses.COLOR_FEEDBACK + MessagesConf.Illnesses.ILLNESS_WORSENED + " " + illness);
                            }
                        }
                    }
                }
            }
        }, 0L, 100L);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(event.getPlayer().getUniqueId());
        for (IllnessEntity illnessEntity : entity.getIllnesses()) {
            addPotionEffects(event.getPlayer(), illnessEntity.getIllness(), illnessEntity.getAmplifier());
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        if (event.getPlayer().getWorld().hasStorm() && Math.random() < BASE_INFECTION_RATE ||
            isNearFromPlayerSick(event.getPlayer(), Illnesses.UPPER_RESPIRATORY_INFECTION) && Math.random() < NEARBY_BASE_INFECTION_RATE) {
            applyIllness(event.getPlayer(), Illnesses.UPPER_RESPIRATORY_INFECTION);
        }
    }

    @EventHandler
    public void on(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if ((entity instanceof Wolf || entity instanceof Bat || entity instanceof Cat)
                && Math.random() < BASE_INFECTION_RATE) {
            MobsUtils.markInfectedByRabies(entity);
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!(event.getDamager() instanceof LivingEntity entity)) {
            return;
        }

        if (MobsUtils.isInfectedWithRabies(entity) && Math.random() < NEARBY_BASE_INFECTION_RATE) {
            applyIllness(player, Illnesses.RABIES);
        }
    }

    private void applyIllness(Player player, Illnesses illness) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        if (entity.hasIllness(illness)) {
            return;
        }
        HibernateUtils.withTransaction(((transaction, session) -> {
            IllnessEntity illnessEntity = new IllnessEntity();
            illnessEntity.setIllness(illness);
            illnessEntity.setGotAt(Date.from(Instant.now()));
            entity.getIllnesses().add(illnessEntity);
            return entity;
        }));
        player.sendMessage(MessagesConf.Illnesses.COLOR_FEEDBACK + MessagesConf.Illnesses.GOT_SICK + " " + illness);
        addPotionEffects(player, illness, 0);
    }

    private void addPotionEffects(Player player, Illnesses illness, int amplifier) {
        removePotionEffects(player, illness);
        for (Map.Entry<PotionEffectType, Integer> effect : illness.getPotionEffects().entrySet()) {
            player.addPotionEffect(new PotionEffect(effect.getKey(), Integer.MAX_VALUE,
                    amplifier > 0 ? amplifier : effect.getValue()));
        }
    }

    private void removePotionEffects(Player player, Illnesses illness) {
        for (PotionEffectType effect : illness.getPotionEffects().keySet()) {
            player.removePotionEffect(effect);
        }
    }

    private boolean isNearFromPlayerSick(Player player, Illnesses illness) {
        for (Player other : Bukkit.getOnlinePlayers()) {
            PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(other.getUniqueId());
            if (player.getLocation().distanceSquared(other.getLocation()) < BASE_INFECTION_DISTANCE &&
                entity.hasIllness(illness)) {
                return true;
            }
        }
        return false;
    }
}
