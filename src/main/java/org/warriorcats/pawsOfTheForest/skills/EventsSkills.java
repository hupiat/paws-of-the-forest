package org.warriorcats.pawsOfTheForest.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventsSkills implements Listener {

    public static final Map<UUID, MenuSkillTreePath> OPENED = new HashMap<>();

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (event.getView().getTitle().equals(MenuSkillTree.TITLE)) {
            event.setCancelled(true);
            handleMainMenuClick(displayName, player);
        }

        MenuSkillTreePath openedMenu = OPENED.get(player.getUniqueId());
        if (openedMenu != null && event.getView().getTitle().equals(openedMenu.getTitle())) {
            event.setCancelled(true);
            handlePerksMenuClick(displayName, player);
        }
    }

    private void handleMainMenuClick(String displayName, Player player) {
        switch (ChatColor.stripColor(displayName)) {
            case "Hunting":
                player.closeInventory();
                OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.HUNTING));
                OPENED.get(player.getUniqueId()).open(player);
                break;

            case "Navigation":
                player.closeInventory();
                OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.NAVIGATION));
                OPENED.get(player.getUniqueId()).open(player);
                break;

            case "Resilience":
                player.closeInventory();
                OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.RESILIENCE));
                OPENED.get(player.getUniqueId()).open(player);
                break;

            case "Herbalist":
                player.closeInventory();
                OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.HERBALIST));
                OPENED.get(player.getUniqueId()).open(player);
                break;

            case "Close":
                player.closeInventory();
                break;
        }
    }

    private void handlePerksMenuClick(String displayName, Player player) {
        switch (ChatColor.stripColor(displayName)) {
            case "Back":
                MenuSkillTree.open(player);
                OPENED.remove(player.getUniqueId());
                break;

            case "Prey Sense":
                // TODO: unlock or activate
                break;

            case "Hunter’s Compass":
                // TODO: unlock or activate
                break;

            case "Low Sweep":
                // TODO: unlock or activate
                break;

            case "Silent Paw":
            case "Blood Hunter":
            case "Efficient Kill":
                // TODO: upgrade tier if possible
                break;

            default:
                break;
        }
    }
}
