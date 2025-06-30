package org.warriorcats.pawsOfTheForest.skills;

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
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;
import org.warriorcats.pawsOfTheForest.utils.PlayersUtils;

import java.util.*;

public class EventsSkillsActives implements Listener {

    public static final int PREY_SENSE_RADIUS = 25;
    public static final int PREY_SENSE_DURATION_S = 5;

    public static final long PREY_SENSE_COOLDOWN_S = 20;
    public static final long HUNTERS_COMPASS_COOLDOWN_S = 60;

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
            }
            event.setCancelled(true);
        }
    }

    private void handlePreySense(PlayerInteractEvent event) {
        if (ItemsUtils.checkForCooldown(event.getPlayer(), event.getItem())) {
            Collection<LivingEntity> livingEntities = event.getPlayer().getWorld()
                    .getNearbyLivingEntities(event.getPlayer().getLocation(), PREY_SENSE_RADIUS);
            for (LivingEntity livingEntity : livingEntities) {
                if (Prey.isPrey(livingEntity)) {
                    livingEntity.addPotionEffect(
                            new PotionEffect(
                                    PotionEffectType.GLOWING,
                                    20 * PREY_SENSE_DURATION_S,
                                    0,
                                    false,
                                    false));
                }
            }
            ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), PREY_SENSE_COOLDOWN_S);
        } else {
            event.getPlayer().sendMessage(ChatColor.RED +
                    MessagesConf.Skills.PLAYER_MESSAGE_COOLDOWN + " " +
                    ItemsUtils.getCooldown(event.getPlayer(), event.getItem()) + "s");
        }
    }

    private void handleHuntersCompass(PlayerInteractEvent event) {
        if (ItemsUtils.checkForCooldown(event.getPlayer(), event.getItem())) {
            Optional<LivingEntity> shorter = Prey.getAllEntities().stream()
                    .min(Comparator.comparingDouble(prey ->
                            prey.getLocation().distanceSquared(event.getPlayer().getLocation())));
            shorter.ifPresent(entity -> event.getPlayer().setCompassTarget(entity.getLocation()));
            ItemsUtils.setCooldown(event.getPlayer(), event.getItem(), HUNTERS_COMPASS_COOLDOWN_S);
        } else {
            event.getPlayer().sendMessage(ChatColor.RED +
                    MessagesConf.Skills.PLAYER_MESSAGE_COOLDOWN + " " +
                    ItemsUtils.getCooldown(event.getPlayer(), event.getItem()) + "s");
        }
    }
}
