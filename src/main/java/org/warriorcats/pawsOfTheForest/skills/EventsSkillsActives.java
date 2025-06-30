package org.warriorcats.pawsOfTheForest.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;
import org.warriorcats.pawsOfTheForest.utils.PlayersUtils;

import java.util.*;

public class EventsSkillsActives implements Listener {

    public static final int PREY_SENSE_RADIUS = 25;
    public static final int PREY_SENSE_DURATION_TICKS = 5 * 20;
    public static final int LOW_SWEEP_CHARGE_DELAY_TICKS = 20; // 1s
    public static final int LOW_SWEEP_SLOWNESS_DURATION_TICKS = 50; // 2.5s
    public static final int LOW_SWEEP_RANGE = 4; // 2.5s

    public static final long PREY_SENSE_COOLDOWN_S = 20;
    public static final long HUNTERS_COMPASS_COOLDOWN_S = 60;
    public static final long LOW_SWEEP_COOLDOWN_S = 15;

    // Handling persistent items (actives skills and noteblock) management

    @EventHandler
    public void on(PlayerRespawnEvent event) {
        PlayersUtils.synchronizeInventory(event.getPlayer());
    }

    @EventHandler
    public void on(PlayerDropItemEvent event) {
        ItemStack dropped = event.getItemDrop().getItemStack();
        if (ItemsUtils.isActiveSkill(event.getPlayer(), dropped) || dropped.getType() == Material.NOTE_BLOCK) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void on(PlayerDeathEvent event) {
        event.getDrops().removeIf(item -> ItemsUtils.isActiveSkill(event.getPlayer(), item));
    }

    // Handling active skills

    @EventHandler
    public void on(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (ItemsUtils.isEmpty(item) || !ItemsUtils.isActiveSkill(event.getPlayer(), item)) return;
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            if (item.getType() == Skills.PREY_SENSE.getIcon()) {
                handlePreySense(event);
            } else if (item.getType() == Skills.HUNTERS_COMPASS.getIcon()) {
               handleHuntersCompass(event);
            } else if (item.getType() == Skills.LOW_SWEEP.getIcon()) {
                handleLowSweep(event);
            }
            event.setCancelled(true);
        }
    }

    private void handlePreySense(PlayerInteractEvent event) {
        withCooldown(() -> {
            Collection<LivingEntity> livingEntities = event.getPlayer().getWorld()
                    .getNearbyLivingEntities(event.getPlayer().getLocation(), PREY_SENSE_RADIUS);
            for (LivingEntity livingEntity : livingEntities) {
                if (Prey.isPrey(livingEntity)) {
                    livingEntity.addPotionEffect(
                            new PotionEffect(
                                    PotionEffectType.GLOWING,
                                    PREY_SENSE_DURATION_TICKS,
                                    0,
                                    false,
                                    false));
                }
            }
            ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), PREY_SENSE_COOLDOWN_S);
            event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_PREY_SENSE);
        }, event);
    }

    private void handleHuntersCompass(PlayerInteractEvent event) {
        withCooldown(() -> {
            Optional<LivingEntity> shorter = Prey.getAllEntities().stream()
                    .min(Comparator.comparingDouble(prey ->
                            prey.getLocation().distanceSquared(event.getPlayer().getLocation())));
            shorter.ifPresent(entity -> {
                event.getPlayer().setCompassTarget(entity.getLocation());
                ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), HUNTERS_COMPASS_COOLDOWN_S);
                event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_HUNTERS_COMPASS);
            });
        }, event);
    }

    private void handleLowSweep(PlayerInteractEvent event) {
        withCooldown(() -> {
            event.getPlayer().sendMessage(ChatColor.GOLD + MessagesConf.Skills.PLAYER_MESSAGE_PREPARE_LOW_SWEEP);

            Bukkit.getScheduler().runTaskLater(
                    PawsOfTheForest.getInstance(),
                    () -> {
                        Optional<LivingEntity> shorter = event.getPlayer().getWorld()
                                .getNearbyLivingEntities(event.getPlayer().getLocation(), LOW_SWEEP_RANGE).stream()
                                .filter(entity -> !entity.getUniqueId().equals(event.getPlayer().getUniqueId()))
                                .min(Comparator.comparingDouble(entity ->
                                        entity.getLocation().distanceSquared(event.getPlayer().getLocation())));
                        if (shorter.isEmpty()) {
                            event.getPlayer().sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_LOW_SWEEP_NO_TARGET);
                            return;
                        }

                        LivingEntity target = shorter.get();
                        target.addPotionEffect(new PotionEffect(
                                PotionEffectType.SLOWNESS,
                                LOW_SWEEP_SLOWNESS_DURATION_TICKS,
                                1,
                                false,
                                true
                        ));

                        target.getWorld().spawnParticle(org.bukkit.Particle.SWEEP_ATTACK, target.getLocation().add(0, 1, 0), 1);
                        target.getWorld().playSound(target.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1f, 1f);
                        event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_LOW_SWEEP);
                        ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), LOW_SWEEP_COOLDOWN_S);
                    },
                    LOW_SWEEP_CHARGE_DELAY_TICKS
            );
        }, event);
    }

    private void withCooldown(Runnable runnable, PlayerInteractEvent event) {
        if (ItemsUtils.checkForCooldown(event.getPlayer(), event.getItem())) {
            runnable.run();
        } else {
            event.getPlayer().sendMessage(ChatColor.RED +
                    MessagesConf.Skills.PLAYER_MESSAGE_COOLDOWN + " " +
                    ItemsUtils.getCooldown(event.getPlayer(), event.getItem()) + "s");
        }
    }
}
