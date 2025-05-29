package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.shops.ShopItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ShopsConf extends AbstractConfiguration {

    public static final String CONFIG_FILE_NAME = "shops_config.yaml";

    public static class Shops {
        // We need the indexes so we are using a List here
        public static final List<ShopItem> SHOP_ITEMS = new ArrayList<>();

        static {
            ConfigurationSection shopSource = yamlSource.getConfigurationSection("shop");
            for (var entry : shopSource.getKeys(false)) {
                ShopItem shopItem = new ShopItem(
                        new ItemStack(Material.valueOf(shopSource.getString(entry + ".item").toUpperCase())),
                        shopSource.getString(entry + ".name"),
                        shopSource.getLong(entry + ".price"),
                        (List<String>) shopSource.getList(entry + ".lore")
                );
                SHOP_ITEMS.add(shopItem);
            }
        }
    }
}
