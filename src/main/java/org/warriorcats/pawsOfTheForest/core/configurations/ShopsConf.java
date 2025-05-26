package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.warriorcats.pawsOfTheForest.shops.Prey;
import org.warriorcats.pawsOfTheForest.shops.ShopItem;

import java.util.HashSet;
import java.util.Set;

public class ShopsConf extends AbstractConfiguration {

    private static final String CONFIG_FILE_NAME = "shops_config.yaml";

    static {
        loadYamlSource(CONFIG_FILE_NAME);
    }

    public static class Preys {
        public static final Set<Prey> PREYS = new HashSet<>();

        static {
            ConfigurationSection preysSource = yamlSource.getConfigurationSection("prey");
            for (var entry : preysSource.getKeys(false)) {
                Prey prey = new Prey();
                prey.setEntityType(EntityType.valueOf(entry.toUpperCase()));
                prey.setXp(preysSource.getDouble(entry + ".xp"));
                prey.setCoins(preysSource.getLong(entry + ".coins"));
                PREYS.add(prey);
            }
        }
    }

    public static class Shops {
        public static final Set<ShopItem> SHOP_ITEMS = new HashSet<>();

        static {
            ConfigurationSection shopSource = yamlSource.getConfigurationSection("shop");
            for (var entry : shopSource.getKeys(false)) {
                ShopItem shopItem = new ShopItem();
                shopItem.setItem(ItemStack.of(Material.valueOf(shopSource.getString(entry + ".item").toUpperCase())));
                shopItem.setName(shopSource.getString(entry + ".name"));
                shopItem.setPrice(shopSource.getLong(entry + ".price"));
                shopItem.setLore(shopSource.getString(entry + ".lore"));
                SHOP_ITEMS.add(shopItem);
            }
        }
    }
}
