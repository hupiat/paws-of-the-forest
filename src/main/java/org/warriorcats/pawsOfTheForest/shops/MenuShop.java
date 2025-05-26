package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;

public abstract class MenuShop {

    public static final String TITLE = "Paw Shop";

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        for (var shopItem : ShopsConf.Shops.SHOP_ITEMS) {
            inv.setItem(ShopsConf.Shops.SHOP_ITEMS.indexOf(shopItem), shopItem.toItemStack());
        }

        player.openInventory(inv);
    }
}
