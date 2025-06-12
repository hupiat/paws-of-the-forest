package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.List;

@Getter
public class MenuSkillTreePath {

    private String title = "Skill Tree";

    public static final ChatColor COLOR_HIGHLIGHT = ChatColor.GRAY;

    public static final int INDEX_PREY_SENSE = 4;
    public static final int INDEX_HUNTERS_COMPASS = 12;
    public static final int INDEX_LOW_SWEEP = 14;
    public static final int INDEX_SILENT_PAW = 20;
    public static final int INDEX_BLOOD_HUNTER = 22;
    public static final int INDEX_EFFICIENT_KILL = 24;

    public static final int INDEX_LOCATION_AWARENESS = 12;
    public static final int INDEX_PATHFINDING_BOOST = 14;
    public static final int INDEX_TRAIL_MEMORY = 20;
    public static final int INDEX_ENDURANCE_TRAVELER = 22;
    public static final int INDEX_CLIMBERS_GRACE = 24;

    public static final int INDEX_HOLD_ON = 12;
    public static final int INDEX_ON_YOUR_PAWS = 14;
    public static final int INDEX_IRON_HIDE = 20;
    public static final int INDEX_IMMUNE_SYSTEM = 22;
    public static final int INDEX_THICK_COAT = 24;
    public static final int INDEX_HEARTY_APPETITE = 30;
    public static final int INDEX_BEAST_OF_BURDEN = 32;

    public static final int INDEX_HERB_KNOWLEDGE = 12;
    public static final int INDEX_BREW_REMEDY = 14;
    public static final int INDEX_QUICK_GATHERER = 20;
    public static final int INDEX_BOTANICAL_LORE = 22;
    public static final int INDEX_CLEAN_PAWS = 24;

    private final SkillBranches branch;

    public MenuSkillTreePath(SkillBranches branch) {
        this.branch = branch;
        title = branch.toString() + " " + title;
    }

    public static Skills getSkillByIndex(int index, SkillBranches branch) {
        return switch (branch) {
            case HUNTING -> switch (index) {
                case INDEX_PREY_SENSE       -> Skills.PREY_SENSE;
                case INDEX_HUNTERS_COMPASS  -> Skills.HUNTERS_COMPASS;
                case INDEX_LOW_SWEEP        -> Skills.LOW_SWEEP;
                case INDEX_SILENT_PAW       -> Skills.SILENT_PAW;
                case INDEX_BLOOD_HUNTER     -> Skills.BLOOD_HUNTER;
                case INDEX_EFFICIENT_KILL   -> Skills.EFFICIENT_KILL;
                default                     -> null;
            };
            case NAVIGATION -> switch (index) {
                case INDEX_LOCATION_AWARENESS -> Skills.LOCATION_AWARENESS;
                case INDEX_PATHFINDING_BOOST  -> Skills.PATHFINDING_BOOST;
                case INDEX_TRAIL_MEMORY       -> Skills.TRAIL_MEMORY;
                case INDEX_ENDURANCE_TRAVELER -> Skills.ENDURANCE_TRAVELER;
                case INDEX_CLIMBERS_GRACE     -> Skills.CLIMBERS_GRACE;
                default                       -> null;
            };
            case RESILIENCE -> switch (index) {
                case INDEX_HOLD_ON          -> Skills.HOLD_ON;
                case INDEX_ON_YOUR_PAWS     -> Skills.ON_YOUR_PAWS;
                case INDEX_IRON_HIDE        -> Skills.IRON_HIDE;
                case INDEX_IMMUNE_SYSTEM    -> Skills.IMMUNE_SYSTEM;
                case INDEX_THICK_COAT       -> Skills.THICK_COAT;
                case INDEX_HEARTY_APPETITE  -> Skills.HEARTY_APPETITE;
                case INDEX_BEAST_OF_BURDEN  -> Skills.BEAST_OF_BURDEN;
                default                     -> null;
            };
            case HERBALIST -> switch (index) {
                case INDEX_HERB_KNOWLEDGE  -> Skills.HERB_KNOWLEDGE;
                case INDEX_BREW_REMEDY     -> Skills.BREW_REMEDY;
                case INDEX_QUICK_GATHERER  -> Skills.QUICK_GATHERER;
                case INDEX_BOTANICAL_LORE  -> Skills.BOTANICAL_LORE;
                case INDEX_CLEAN_PAWS      -> Skills.CLEAN_PAWS;
                default                    -> null;
            };
        };
    }

    public void open(Player player) {
        Inventory menu = Bukkit.createInventory(null, 45, title);

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());

            switch (branch) {
                case HUNTING -> drawHuntingBranch(menu, entity);
                case NAVIGATION -> drawNavigationBranch(menu, entity);
                case RESILIENCE -> drawResilienceBranch(menu, entity);
                case HERBALIST -> drawHerbalistBranch(menu, entity);
            }
        }

        menu.setItem(MenuSkillTree.INDEX_BACK, createSimpleItem(Material.BARRIER, "§cBack", List.of("§7Return to Skill Tree Menu")));
        menu.setItem(MenuSkillTree.INDEX_SKILLS_POINTS, MenuSkillTree.createSkillPointsItemStack(player));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.2f);
    }

    private static void drawHuntingBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_PREY_SENSE, createSkillItem(Material.GHAST_TEAR, Skills.PREY_SENSE.toString(), "Reveal nearby prey (5s glowing, 25 blocks)",
                entity.hasAbility(Skills.PREY_SENSE), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_HUNTERS_COMPASS, createSkillItem(Material.COMPASS, Skills.HUNTERS_COMPASS.toString(), "Points to closest huntable target (updates every 60s)",
                entity.hasAbility(Skills.HUNTERS_COMPASS), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_LOW_SWEEP, createSkillItem(Material.RABBIT_FOOT, Skills.LOW_SWEEP.toString(), "Applies Slowness II to target (2.5s)",
                entity.hasAbility(Skills.LOW_SWEEP), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_SILENT_PAW, createTieredItem(Material.LEATHER, Skills.SILENT_PAW.toString(), "Reduces movement sound radius",
                entity.getAbilityPerk(Skills.SILENT_PAW), Skills.SILENT_PAW.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_BLOOD_HUNTER, createTieredItem(Material.REDSTONE, Skills.BLOOD_HUNTER.toString(), "Higher chance for quality prey",
                entity.getAbilityPerk(Skills.BLOOD_HUNTER), Skills.BLOOD_HUNTER.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_EFFICIENT_KILL, createTieredItem(Material.COOKED_BEEF, Skills.EFFICIENT_KILL.toString(), "More XP/food on stealth kills",
                entity.getAbilityPerk(Skills.EFFICIENT_KILL), Skills.EFFICIENT_KILL.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));
    }

    private static void drawNavigationBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_LOCATION_AWARENESS, createSkillItem(Material.FILLED_MAP, Skills.LOCATION_AWARENESS.toString(), "Cycle compass between known waypoints",
                entity.hasAbility(Skills.LOCATION_AWARENESS), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_PATHFINDING_BOOST, createSkillItem(Material.FEATHER, Skills.PATHFINDING_BOOST.toString(), "Grants Speed I and Jump I outside combat",
                entity.hasAbility(Skills.PATHFINDING_BOOST), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_TRAIL_MEMORY, createTieredItem(Material.PAPER, Skills.TRAIL_MEMORY.toString(), "Recall landmarks instantly",
                entity.getAbilityPerk(Skills.TRAIL_MEMORY), Skills.TRAIL_MEMORY.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_ENDURANCE_TRAVELER, createTieredItem(Material.COOKED_PORKCHOP, Skills.ENDURANCE_TRAVELER.toString(), "Reduce hunger loss out of combat",
                entity.getAbilityPerk(Skills.ENDURANCE_TRAVELER), Skills.ENDURANCE_TRAVELER.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_CLIMBERS_GRACE, createTieredItem(Material.LADDER, Skills.CLIMBERS_GRACE.toString(), "Jump higher passively",
                entity.getAbilityPerk(Skills.CLIMBERS_GRACE), Skills.CLIMBERS_GRACE.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));
    }

    private static void drawResilienceBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_HOLD_ON, createSkillItem(Material.TOTEM_OF_UNDYING, Skills.HOLD_ON.toString(), "Avoids death and enters downed state",
                entity.hasAbility(Skills.HOLD_ON), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_ON_YOUR_PAWS, createSkillItem(Material.GOLDEN_APPLE, Skills.ON_YOUR_PAWS.toString(), "Revive downed ally after 8s",
                entity.hasAbility(Skills.ON_YOUR_PAWS), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_IRON_HIDE, createTieredItem(Material.IRON_CHESTPLATE, Skills.IRON_HIDE.toString(), "+1 armor per tier",
                entity.getAbilityPerk(Skills.IRON_HIDE), Skills.IRON_HIDE.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_IMMUNE_SYSTEM, createTieredItem(Material.SPIDER_EYE, Skills.IMMUNE_SYSTEM.toString(), "10% illness resistance per tier",
                entity.getAbilityPerk(Skills.IMMUNE_SYSTEM), Skills.IMMUNE_SYSTEM.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_THICK_COAT, createTieredItem(Material.SNOWBALL, Skills.THICK_COAT.toString(), "Cold resistance, weak to fire",
                entity.getAbilityPerk(Skills.THICK_COAT), Skills.THICK_COAT.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_HEARTY_APPETITE, createTieredItem(
                Material.COOKED_MUTTON,
                Skills.HEARTY_APPETITE.toString(),
                "Increases food saturation restoration per tier",
                entity.getAbilityPerk(Skills.HEARTY_APPETITE),
                Skills.HEARTY_APPETITE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_BEAST_OF_BURDEN, createTieredItem(
                Material.CHEST,
                Skills.BEAST_OF_BURDEN.toString(),
                "Adds inventory capacity per tier",
                entity.getAbilityPerk(Skills.BEAST_OF_BURDEN),
                Skills.BEAST_OF_BURDEN.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private static void drawHerbalistBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_HERB_KNOWLEDGE, createSkillItem(Material.FERN, Skills.HERB_KNOWLEDGE.toString(), "Highlights herbs within 15 blocks",
                entity.hasAbility(Skills.HERB_KNOWLEDGE), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_BREW_REMEDY, createSkillItem(Material.BREWING_STAND, Skills.BREW_REMEDY.toString(), "Brew cures using collected herbs",
                entity.hasAbility(Skills.BREW_REMEDY), SkillBranches.UNLOCK_SKILL));

        menu.setItem(INDEX_QUICK_GATHERER, createTieredItem(Material.SHEARS, Skills.QUICK_GATHERER.toString(), "Collect herbs faster",
                entity.getAbilityPerk(Skills.QUICK_GATHERER), Skills.QUICK_GATHERER.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_BOTANICAL_LORE, createTieredItem(Material.WRITABLE_BOOK, Skills.BOTANICAL_LORE.toString(), "Unlock new recipes or uses",
                entity.getAbilityPerk(Skills.BOTANICAL_LORE), Skills.BOTANICAL_LORE.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));

        menu.setItem(INDEX_CLEAN_PAWS, createTieredItem(Material.HONEYCOMB, Skills.CLEAN_PAWS.toString(), "Reduce self-infection risk",
                entity.getAbilityPerk(Skills.CLEAN_PAWS), Skills.CLEAN_PAWS.getMaxTiers(), SkillBranches.UNLOCK_SKILL_TIER));
    }

    private static ItemStack createSkillItem(Material material, String name, String desc, boolean unlocked, double xpCost) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(unlocked ? "§a" + name : "§e" + name);
        meta.setLore(List.of(
                COLOR_HIGHLIGHT + desc,
                "",
                unlocked ? "§aUnlocked" : "§6Cost: " + xpCost + " XP levels",
                unlocked ? "" : "§eClick to unlock"
        ));
        item.setItemMeta(meta);

        return item;
    }

    private static ItemStack createTieredItem(Material material, String name, String desc, double currentXp, int maxTier, double xpPerTier) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        int currentTier = (int) Math.round(currentXp / xpPerTier);

        String display = (currentTier >= maxTier ? "§a" : "§e") + name + COLOR_HIGHLIGHT + " (Tier " + currentTier + "/" + maxTier + ")";
        meta.setDisplayName(display);
        meta.setLore(List.of(
                COLOR_HIGHLIGHT + desc,
                "",
                currentTier >= maxTier
                        ? "§aMax Tier Reached"
                        : "§6Next Tier Cost: " + xpPerTier + " XP levels",
                currentTier >= maxTier ? "" : "§eClick to upgrade"
        ));

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createSimpleItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
