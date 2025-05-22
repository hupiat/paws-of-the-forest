package org.warriorcats.pawsOfTheForest.core.settings;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public abstract class SettingsMenu {

    public static Inventory create(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "⚙️ Chat Settings");

        inv.setItem(10, createToggleItem("RP Chat", isRpEnabled(player)));
        inv.setItem(12, createChatDropdown("Toggled Chat", getToggledChat(player)));

        return inv;
    }

    public static boolean isRpEnabled(Player player) {
        return fetchSettings(player).isShowRoleplay();
    }

    public static ChatChannel getToggledChat(Player player) {
        return fetchSettings(player).getToggledChat();
    }

    public static ChatChannel getNextChat(ChatChannel current) {
        ChatChannel[] values = ChatChannel.values();
        int index = (current.ordinal() + 1) % values.length;
        return values[index];
    }

    private static ItemStack createToggleItem(String name, boolean enabled) {
        Material mat = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name + ": " + (enabled ? ChatColor.GREEN + "ON" : ChatColor.RED + "OFF"));
        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createChatDropdown(String name, ChatChannel selected) {
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + name + ": " + ChatColor.AQUA + selected.name());
        item.setItemMeta(meta);
        return item;
    }

    private static SettingsEntity fetchSettings(Player player) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            return session.get(PlayerEntity.class, player.getUniqueId()).getSettings();
        }
    }
}

