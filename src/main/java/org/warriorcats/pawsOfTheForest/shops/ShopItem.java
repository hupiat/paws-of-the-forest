package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public record ShopItem(ItemStack item, String name, long price, List<String> lore) {

    public ItemStack toItemStack() {
        ItemStack clone = new ItemStack(item.getType());
        ItemMeta meta = clone.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> fullLore = new ArrayList<>(lore);
            fullLore.add("");
            fullLore.add(ChatColor.GREEN + "Price : " + price + " Paw Coins");
            meta.setLore(fullLore);
            clone.setItemMeta(meta);
        }
        return clone;
    }
}
