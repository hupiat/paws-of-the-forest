package org.warriorcats.pawsOfTheForest.skills;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.List;

public abstract class MenuSkillTree {
    public static final String TITLE = "Skill Trees";

    public static final String TEXT_CLICK_TO_OPEN = MessagesConf.Skills.COLOR_DESCRIPTION + "Click to open";
    public static final String TEXT_CLOSE = MessagesConf.Skills.COLOR_DESCRIPTION + "Close";
    public static final String TEXT_SKILL_POINTS = MessagesConf.Skills.COLOR_DESCRIPTION + "Skill Points";

    public static void open(Player player) {
        Inventory menu = Bukkit.createInventory(null, 45, TITLE);

        menu.setItem(10, createMenuItem(Material.RABBIT, SkillBranches.HUNTING.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION +
                MessagesConf.Skills.HUNTING_DESCRIPTION,
                "",
                TEXT_CLICK_TO_OPEN
        )));

        menu.setItem(12, createMenuItem(Material.COMPASS, SkillBranches.NAVIGATION.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION +
                MessagesConf.Skills.NAVIGATION_DESCRIPTION,
                "",
                TEXT_CLICK_TO_OPEN
        )));

        menu.setItem(14, createMenuItem(Material.SHIELD, SkillBranches.RESILIENCE.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION +
                MessagesConf.Skills.RESILIENCE_DESCRIPTION,
                "",
                TEXT_CLICK_TO_OPEN
        )));

        menu.setItem(16, createMenuItem(Material.FERN, SkillBranches.HERBALIST.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION +
                MessagesConf.Skills.HERBALIST_DESCRIPTION,
                "",
                TEXT_CLICK_TO_OPEN
        )));

        menu.setItem(36, createMenuItem(Material.BARRIER, TEXT_CLOSE, List.of(MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.MENU_EXIT)));
        menu.setItem(44, createSkillPointsItemStack(player));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 1.2f);
    }

    public static double getSkillPoints(Player player) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            return entity.getXpPerks();
        }
    }

    public static ItemStack createSkillPointsItemStack(Player player) {
        return createMenuItem(Material.NETHER_STAR, TEXT_SKILL_POINTS, List.of(MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.MENU_SKILL_POINTS + " " + getSkillPoints(player)));
    }

    private static ItemStack createMenuItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
