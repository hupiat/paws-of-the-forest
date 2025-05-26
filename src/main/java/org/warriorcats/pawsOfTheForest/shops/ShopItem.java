package org.warriorcats.pawsOfTheForest.shops;

import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
public class ShopItem {
    private ItemStack item;
    private String name;
    private long price;
    private String lore;
}
