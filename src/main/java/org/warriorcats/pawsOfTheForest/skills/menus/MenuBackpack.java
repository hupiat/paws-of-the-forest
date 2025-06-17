package org.warriorcats.pawsOfTheForest.skills.menus;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.EventsSkills;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;

public abstract class MenuBackpack {

    public static final String TITLE = "Beast of Burden";

    public static void open(Player player, int tier) {
        Inventory menu = Bukkit.createInventory(player, tier * EventsSkills.BEAST_OF_BURDEN_TIER_VALUE, TITLE);

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            if (entity.getBackpackData() != null) {
                ItemStack[] items = ItemsUtils.deserializeItemStackArray(entity.getBackpackData());
                int counter = 0;
                for (ItemStack item : items) {
                    menu.setItem(counter, item);
                    counter++;
                }
            }
        }

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 1.2f);
    }
}
