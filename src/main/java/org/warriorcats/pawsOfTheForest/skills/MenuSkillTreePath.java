package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;
import org.bukkit.Bukkit;
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

    public static final int INDEX_HERB_KNOWLEDGE = 12;
    public static final int INDEX_BREW_REMEDY = 14;
    public static final int INDEX_QUICK_GATHERER = 20;
    public static final int INDEX_BOTANICAL_LORE = 22;
    public static final int INDEX_CLEAN_PAWS = 24;

    private SkillBranches branch;

    public MenuSkillTreePath(SkillBranches branch) {
        this.branch = branch;
        title = branch.toString() + " " + title;
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

        menu.setItem(36, createSimpleItem(Material.BARRIER, "§cBack", List.of("§7Return to Skill Tree Menu")));
        menu.setItem(44, MenuSkillTree.createSkillPointsItemStack(player));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.2f);
    }

    private static void drawHuntingBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_PREY_SENSE, createSkillItem(Material.GHAST_TEAR, "Prey Sense", "Reveal nearby prey (5s glowing, 25 blocks)",
                entity.hasAbility("Prey Sense"), 8));

        menu.setItem(INDEX_HUNTERS_COMPASS, createSkillItem(Material.COMPASS, "Hunter’s Compass", "Points to closest huntable target (updates every 60s)",
                entity.hasAbility("Hunter’s Compass"), 8));

        menu.setItem(INDEX_LOW_SWEEP, createSkillItem(Material.RABBIT_FOOT, "Low Sweep", "Applies Slowness II to target (2.5s)",
                entity.hasAbility("Low Sweep"), 8));

        menu.setItem(INDEX_SILENT_PAW, createTieredItem(Material.LEATHER, "Silent Paw", "Reduces movement sound radius",
                (int) entity.getAbilityPerk("Silent Paw"), 3, 2));

        menu.setItem(INDEX_BLOOD_HUNTER, createTieredItem(Material.REDSTONE, "Blood Hunter", "Higher chance for quality prey",
                (int) entity.getAbilityPerk("Blood Hunter"), 4, 2));

        menu.setItem(INDEX_EFFICIENT_KILL, createTieredItem(Material.COOKED_BEEF, "Efficient Kill", "More XP/food on stealth kills",
                (int) entity.getAbilityPerk("Efficient Kill"), 3, 2));
    }

    private static void drawNavigationBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_LOCATION_AWARENESS, createSkillItem(Material.FILLED_MAP, "Location Awareness", "Cycle compass between known waypoints",
                entity.hasAbility("Location Awareness"), 8));

        menu.setItem(INDEX_PATHFINDING_BOOST, createSkillItem(Material.FEATHER, "Pathfinding Boost", "Grants Speed I and Jump I outside combat",
                entity.hasAbility("Pathfinding Boost"), 8));

        menu.setItem(INDEX_TRAIL_MEMORY, createTieredItem(Material.PAPER, "Trail Memory", "Recall landmarks instantly",
                (int) entity.getAbilityPerk("Trail Memory"), 3, 2));

        menu.setItem(INDEX_ENDURANCE_TRAVELER, createTieredItem(Material.COOKED_PORKCHOP, "Endurance Traveler", "Reduce hunger loss out of combat",
                (int) entity.getAbilityPerk("Endurance Traveler"), 4, 2));

        menu.setItem(INDEX_CLIMBERS_GRACE, createTieredItem(Material.LADDER, "Climber’s Grace", "Jump higher passively",
                (int) entity.getAbilityPerk("Climber’s Grace"), 2, 2));
    }

    private static void drawResilienceBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_HOLD_ON, createSkillItem(Material.TOTEM_OF_UNDYING, "Hold On!", "Avoids death and enters downed state",
                entity.hasAbility("Hold On!"), 8));

        menu.setItem(INDEX_ON_YOUR_PAWS, createSkillItem(Material.GOLDEN_APPLE, "On Your Paws!", "Revive downed ally after 8s",
                entity.hasAbility("On Your Paws!"), 8));

        menu.setItem(INDEX_IRON_HIDE, createTieredItem(Material.IRON_CHESTPLATE, "Iron Hide", "+1 armor per tier",
                (int) entity.getAbilityPerk("Iron Hide"), 3, 2));

        menu.setItem(INDEX_IMMUNE_SYSTEM, createTieredItem(Material.SPIDER_EYE, "Immune System", "10% illness resistance per tier",
                (int) entity.getAbilityPerk("Immune System"), 3, 2));

        menu.setItem(INDEX_THICK_COAT, createTieredItem(Material.SNOWBALL, "Thick Coat", "Cold resistance, weak to fire",
                (int) entity.getAbilityPerk("Thick Coat"), 2, 2));
    }

    private static void drawHerbalistBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_HERB_KNOWLEDGE, createSkillItem(Material.FERN, "Herb Knowledge", "Highlights herbs within 15 blocks",
                entity.hasAbility("Herb Knowledge"), 8));

        menu.setItem(INDEX_BREW_REMEDY, createSkillItem(Material.BREWING_STAND, "Brew Remedy", "Brew cures using collected herbs",
                entity.hasAbility("Brew Remedy"), 8));

        menu.setItem(INDEX_QUICK_GATHERER, createTieredItem(Material.SHEARS, "Quick Gatherer", "Collect herbs faster",
                (int) entity.getAbilityPerk("Quick Gatherer"), 3, 2));

        menu.setItem(INDEX_BOTANICAL_LORE, createTieredItem(Material.WRITABLE_BOOK, "Botanical Lore", "Unlock new recipes or uses",
                (int) entity.getAbilityPerk("Botanical Lore"), 3, 2));

        menu.setItem(INDEX_CLEAN_PAWS, createTieredItem(Material.HONEYCOMB, "Clean Paws", "Reduce self-infection risk",
                (int) entity.getAbilityPerk("Clean Paws"), 2, 2));
    }

    private static ItemStack createSkillItem(Material material, String name, String desc, boolean unlocked, int xpCost) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(unlocked ? "§a" + name + " §7(✔)" : "§7" + name);
        meta.setLore(List.of(
                "§7" + desc,
                "",
                unlocked ? "§aUnlocked" : "§6Cost: " + xpCost + " XP levels",
                "§eClick to " + (unlocked ? "view" : "unlock")
        ));
        item.setItemMeta(meta);

        if (unlocked) item.addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 1);
        return item;
    }

    private static ItemStack createTieredItem(Material material, String name, String desc, int currentTier, int maxTier, int xpPerTier) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        String display = (currentTier >= maxTier ? "§a" : "§e") + name + " §7(Tier " + currentTier + "/" + maxTier + ")";
        meta.setDisplayName(display);
        meta.setLore(List.of(
                "§7" + desc,
                "",
                currentTier >= maxTier
                        ? "§aMax Tier Reached"
                        : "§6Next Tier Cost: " + xpPerTier + " XP levels",
                "§eClick to " + (currentTier >= maxTier ? "inspect" : "upgrade")
        ));

        item.setItemMeta(meta);
        if (currentTier > 0) item.addUnsafeEnchantment(Enchantment.LUCK_OF_THE_SEA, 1);
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
