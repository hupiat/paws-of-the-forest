package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.List;

public abstract class MenuShop {

    public static final String TITLE = "Paw Shop";

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, TITLE);

        for (var shopItem : ShopsConf.Shops.SHOP_ITEMS) {
            inv.setItem(ShopsConf.Shops.SHOP_ITEMS.indexOf(shopItem), shopItem.toItemStack());
        }

        inv.setItem(26, createCoinsItem(player));

        player.openInventory(inv);
    }

    private static ItemStack createCoinsItem(Player player) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        ItemStack coinItem = new ItemStack(Material.SUNFLOWER);
        ItemMeta meta = coinItem.getItemMeta();
        meta.setDisplayName(MessagesConf.Shops.COLOR_COINS_TEXT + MessagesConf.Shops.COINS + " " +
                MessagesConf.Shops.COLOR_COINS + entity.getCoins());
        meta.setLore(List.of(MessagesConf.Shops.COLOR_COINS_LORE + MessagesConf.Shops.COINS_LORE_1,
                MessagesConf.Shops.COLOR_COINS_LORE + MessagesConf.Shops.COINS_LORE_2));
        coinItem.setItemMeta(meta);
        return coinItem;
    }
}
