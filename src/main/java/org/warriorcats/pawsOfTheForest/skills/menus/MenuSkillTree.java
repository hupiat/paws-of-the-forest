package org.warriorcats.pawsOfTheForest.skills.menus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.warriorcats.pawsOfTheForest.clans.Clans;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;

import java.util.ArrayList;
import java.util.List;

public abstract class MenuSkillTree {
    public static final String TITLE = "Skill Trees";

    public static final int INDEX_BACK = 36;
    public static final int INDEX_SKILLS_POINTS = 44;

    public static final int INDEX_HUNTING = 1;
    public static final int INDEX_NAVIGATION = 3;
    public static final int INDEX_RESILIENCE = 5;
    public static final int INDEX_HERBALIST = 7;

    public static final int INDEX_KITTYPET = 10;
    public static final int INDEX_LONER = 12;
    public static final int INDEX_ROGUE = 14;
    public static final int INDEX_CITY_CAT = 16;

    public static final int INDEX_BREEZE_CLAN = 19;
    public static final int INDEX_ECHO_CLAN = 21;
    public static final int INDEX_CREEK_CLAN = 23;
    public static final int INDEX_SHADE_CLAN = 25;

    public static final String TEXT_CLICK_TO_OPEN = MessagesConf.Skills.COLOR_DESCRIPTION + "Click to open";
    public static final String TEXT_CLOSE = MessagesConf.Skills.COLOR_DESCRIPTION + "Close";
    public static final String TEXT_DISABLED = MessagesConf.Skills.COLOR_DESCRIPTION + "Unavailable";
    public static final String TEXT_SKILL_POINTS = MessagesConf.Skills.COLOR_DESCRIPTION + "Skill Points";

    public static void open(Player player) {
        Inventory menu = Bukkit.createInventory(null, 45, TITLE);

        menu.setItem(INDEX_HUNTING, createMenuItem(Material.RABBIT, SkillBranches.HUNTING.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.HUNTING_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_NAVIGATION, createMenuItem(Material.COMPASS, SkillBranches.NAVIGATION.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.NAVIGATION_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_RESILIENCE, createMenuItem(Material.SHIELD, SkillBranches.RESILIENCE.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.RESILIENCE_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_HERBALIST, createMenuItem(Material.FERN, SkillBranches.HERBALIST.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.HERBALIST_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_KITTYPET, createMenuItem(Material.MILK_BUCKET, SkillBranches.KITTYPET.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.KITTYPET_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_LONER, createMenuItem(Material.LEATHER, SkillBranches.LONER.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.LONER_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_ROGUE, createMenuItem(Material.IRON_SWORD, SkillBranches.ROGUE.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.ROGUE_DESCRIPTION
        ), true, true));

        menu.setItem(INDEX_CITY_CAT, createMenuItem(Material.STONE_BRICKS, SkillBranches.CITY_CAT.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.CITY_CAT_DESCRIPTION
        ), true, true));

        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());

        menu.setItem(INDEX_BREEZE_CLAN, createMenuItem(Material.SUGAR, SkillBranches.BREEZE_CLAN.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.BREEZE_CLAN_DESCRIPTION
        ), true, entity.getClan() == Clans.BREEZE));

        menu.setItem(INDEX_ECHO_CLAN, createMenuItem(Material.OAK_LEAVES, SkillBranches.ECHO_CLAN.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.ECHO_CLAN_DESCRIPTION
        ), true, entity.getClan() == Clans.ECHO));

        menu.setItem(INDEX_CREEK_CLAN, createMenuItem(Material.KELP, SkillBranches.CREEK_CLAN.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.CREEK_CLAN_DESCRIPTION
        ), true, entity.getClan() == Clans.CREEK));

        menu.setItem(INDEX_SHADE_CLAN, createMenuItem(Material.ENDER_PEARL, SkillBranches.SHADE_CLAN.toString(), List.of(
                MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.SHADE_CLAN_DESCRIPTION
        ), true, entity.getClan() == Clans.SHADE));

        menu.setItem(INDEX_BACK, createMenuItem(Material.BARRIER, TEXT_CLOSE, List.of(MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.MENU_EXIT), false, true));
        menu.setItem(INDEX_SKILLS_POINTS, createSkillPointsItemStack(player));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 0.7f, 1.2f);
    }

    public static double getSkillPoints(Player player) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
        return entity.getXpPerks();
    }

    public static ItemStack createSkillPointsItemStack(Player player) {
        return createMenuItem(Material.NETHER_STAR, TEXT_SKILL_POINTS, List.of(MessagesConf.Skills.COLOR_DESCRIPTION + MessagesConf.Skills.MENU_SKILL_POINTS + " " + getSkillPoints(player)), false, true);
    }

    private static ItemStack createMenuItem(Material material, String name, List<String> lore, boolean additionalLore, boolean enabled) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setDisplayName((enabled ? "" : ChatColor.GRAY) + name);
        meta.setLore(new ArrayList<>(lore) {{
            if (additionalLore) {
                add("");
                add(enabled ? TEXT_CLICK_TO_OPEN : TEXT_DISABLED);
            }
        }});
        item.setItemMeta(meta);
        return item;
    }
}
