package org.warriorcats.pawsOfTheForest.skills;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;
import org.warriorcats.pawsOfTheForest.utils.PlayersUtils;

import java.util.ArrayList;
import java.util.List;

public class EventsSkillsActives implements Listener {

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
}
