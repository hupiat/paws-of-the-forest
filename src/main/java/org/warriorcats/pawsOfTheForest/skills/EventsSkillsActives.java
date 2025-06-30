package org.warriorcats.pawsOfTheForest.skills;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;
import org.warriorcats.pawsOfTheForest.utils.PlayersUtils;

import java.util.*;

public class EventsSkillsActives implements Listener {

    public static final int PREY_SENSE_RADIUS = 25;
    public static final int PREY_SENSE_DURATION_TICKS = 5 * 20;
    public static final int LOW_SWEEP_RANGE = 4;
    public static final int LOW_SWEEP_CHARGE_DELAY_TICKS = 20; // 1s
    public static final int LOW_SWEEP_SLOWNESS_DURATION_TICKS = 50; // 2.5s
    public static final int PATHFINDING_BOOST_DURATION_TICKS = 10 * 20;
    public static final double HOLD_ON_RADIUS = 50;
    public static final long HOLD_ON_DURATION_TICKS = 2 * 60 * 20; // 2 minutes

    public static final long HOLD_ON_COOLDOWN_S = 600; // 2 minutes
    public static final long PREY_SENSE_COOLDOWN_S = 20;
    public static final long HUNTERS_COMPASS_COOLDOWN_S = 60;
    public static final long LOW_SWEEP_COOLDOWN_S = 15;
    public static final long PATHFINDING_BOOST_COOLDOWN_S = 20;

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

        // Handling Resilience branch (downed state)

        Player dying = event.getEntity();

        if (PlayersUtils.isDowned(dying)) return;
        if (PlayersUtils.hasHoldOnOnCooldown(dying)) return;

        Optional<Player> protector = dying.getWorld().getPlayers().stream()
                .filter(p -> !p.equals(dying))
                .filter(p -> p.getLocation().distanceSquared(dying.getLocation()) <= HOLD_ON_RADIUS * HOLD_ON_RADIUS)
                .filter(p -> {
                    PlayerEntity pe = EventsCore.PLAYERS_CACHE.get(p.getUniqueId());
                    PlayerEntity dyingEntity = EventsCore.PLAYERS_CACHE.get(dying.getUniqueId());
                    return pe.hasAbility(Skills.HOLD_ON) && pe.getClan() == dyingEntity.getClan();
                })
                .findAny();

        if (protector.isEmpty()) return;

        Player helper = protector.get();

        event.setCancelled(true);
        dying.setHealth(1);
        dying.setFireTicks(0);
        dying.setFoodLevel(1);
        dying.setVelocity(dying.getVelocity().multiply(0));

        PlayersUtils.setDowned(dying, true);
        PlayersUtils.markHoldOnUsed(dying);

        helper.sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_HOLD_ON + " " + dying.getName());
        dying.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_HOLD_ON);

        handleDownedState(dying, HOLD_ON_DURATION_TICKS);
    }

    @EventHandler
    public void on(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (PlayersUtils.isDowned(player)) {
            handleDownedState(player, PlayersUtils.getDownedCooldown(player));
        }
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (PlayersUtils.isDowned(player)) {
            player.setSneaking(true);
            player.setWalkSpeed(0.05f);
            if (event.getFrom().getY() < event.getTo().getY()) {
                event.setTo(event.getFrom());
            }
        }
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
            } else if (item.getType() == Skills.PATHFINDING_BOOST.getIcon()) {
                handlePathfindingBoost(event);
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

    private void handlePathfindingBoost(PlayerInteractEvent event) {
        withCooldown(() -> {
            if (EventsCore.PLAYERS_FIGHTING.contains(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_PATHFINDING_BOOST_IN_COMBAT);
                return;
            }
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PATHFINDING_BOOST_DURATION_TICKS, 0, false, false));
            event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, PATHFINDING_BOOST_DURATION_TICKS, 0, false, false));
            event.getPlayer().sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_APPLIED_PATHFINDING_BOOST);
            ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), PATHFINDING_BOOST_COOLDOWN_S);
        }, event);
    }

    private void handleDownedState(Player dying, long delay) {
        Bukkit.getScheduler().runTaskLater(PawsOfTheForest.getInstance(), () -> {
            if (!dying.isOnline()) return;
            if (PlayersUtils.isDowned(dying)) {
                dying.setHealth(0);
                PlayersUtils.setDowned(dying, false);
                dying.sendMessage(ChatColor.DARK_RED + MessagesConf.Skills.PLAYER_MESSAGE_HOLD_ON_SUCCUMBED);
            }
        }, delay);
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
