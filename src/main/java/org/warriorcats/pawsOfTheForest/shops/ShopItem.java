package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.inventory.ItemStack;

public record ShopItem(ItemStack item, String name, long price, String lore) {
}
