package org.warriorcats.pawsOfTheForest.core.settings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.Arrays;

public abstract class MenuSettings {

    public static final String TITLE = "Chat Settings";

    public static final int INDEX_RP_TOGGLE = 10;
    public static final int INDEX_CHAT_DROPDOWN = 12;

    public static Inventory create(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        inv.setItem(INDEX_RP_TOGGLE, createToggleItem("RP Chat", isRpEnabled(player)));
        inv.setItem(INDEX_CHAT_DROPDOWN, createChatDropdown("Toggled Chat", getToggledChat(player)));

        return inv;
    }

    public static boolean isRpEnabled(Player player) {
        return fetchSettings(player).isShowRoleplay();
    }

    public static ChatChannels getToggledChat(Player player) {
        return fetchSettings(player).getToggledChat();
    }

    public static ChatChannels getNextChat(Player player, ChatChannels current) {
        ChatChannels[] values;
        SettingsEntity settings = fetchSettings(player);
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        values = Arrays.stream(ChatChannels.values()).filter(channel -> {
            boolean filtered = true;
            if (!settings.isShowRoleplay()) {
                filtered = filtered && !ChatChannels.isRoleplay(channel);
            }
            if (entity.getClan() == null) {
                filtered = filtered && channel != ChatChannels.CLAN;
            }
            return filtered;
        }).toArray(ChatChannels[]::new);
        int currentIndex = Arrays.asList(values).indexOf(current);
        int nextIndex = (currentIndex + 1) % values.length;
        return values[nextIndex];
    }

    private static ItemStack createToggleItem(String name, boolean enabled) {
        Material mat = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name + ": " + (enabled ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createChatDropdown(String name, ChatChannels selected) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name + ": " + ChatColor.AQUA + selected.toString());
        item.setItemMeta(meta);
        return item;
    }

    private static SettingsEntity fetchSettings(Player player) {
        return EventsCore.PLAYERS_CACHE.get(player.getUniqueId()).getSettings();
    }
}

