package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.skills.menus.MenuSkillTreePath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ItemsUtils {

    public static final NamespacedKey META_COOLDOWN_KEY = new NamespacedKey(PawsOfTheForest.getInstance(), "cooldown");
    public static final NamespacedKey META_COOLDOWN_SECONDARY_KEY = new NamespacedKey(PawsOfTheForest.getInstance(), "cooldown_secondary");
    public static final NamespacedKey META_BAD_PREY_KEY = new NamespacedKey(PawsOfTheForest.getInstance(), "bad_prey");

    public static final Set<Material> URBAN_BLOCKS = Set.of(
            Material.STONE,
            Material.COBBLESTONE,
            Material.STONE_BRICKS,
            Material.MOSSY_STONE_BRICKS,
            Material.CRACKED_STONE_BRICKS,
            Material.CHISELED_STONE_BRICKS,
            Material.ANDESITE,
            Material.POLISHED_ANDESITE,
            Material.DIORITE,
            Material.POLISHED_DIORITE,
            Material.GRANITE,
            Material.POLISHED_GRANITE,
            Material.DEEPSLATE,
            Material.COBBLED_DEEPSLATE,
            Material.POLISHED_DEEPSLATE,
            Material.DEEPSLATE_BRICKS,
            Material.DEEPSLATE_TILES,
            Material.GRAVEL,
            Material.SMOOTH_STONE,

            Material.WHITE_CONCRETE,
            Material.ORANGE_CONCRETE,
            Material.MAGENTA_CONCRETE,
            Material.LIGHT_BLUE_CONCRETE,
            Material.YELLOW_CONCRETE,
            Material.LIME_CONCRETE,
            Material.PINK_CONCRETE,
            Material.GRAY_CONCRETE,
            Material.LIGHT_GRAY_CONCRETE,
            Material.CYAN_CONCRETE,
            Material.PURPLE_CONCRETE,
            Material.BLUE_CONCRETE,
            Material.BROWN_CONCRETE,
            Material.GREEN_CONCRETE,
            Material.RED_CONCRETE,
            Material.BLACK_CONCRETE,

            Material.WHITE_CONCRETE_POWDER,
            Material.ORANGE_CONCRETE_POWDER,
            Material.MAGENTA_CONCRETE_POWDER,
            Material.LIGHT_BLUE_CONCRETE_POWDER,
            Material.YELLOW_CONCRETE_POWDER,
            Material.LIME_CONCRETE_POWDER,
            Material.PINK_CONCRETE_POWDER,
            Material.GRAY_CONCRETE_POWDER,
            Material.LIGHT_GRAY_CONCRETE_POWDER,
            Material.CYAN_CONCRETE_POWDER,
            Material.PURPLE_CONCRETE_POWDER,
            Material.BLUE_CONCRETE_POWDER,
            Material.BROWN_CONCRETE_POWDER,
            Material.GREEN_CONCRETE_POWDER,
            Material.RED_CONCRETE_POWDER,
            Material.BLACK_CONCRETE_POWDER
    );

    public static final Set<Material> TRASH_BLOCKS = Set.of(
            Material.COARSE_DIRT,
            Material.PODZOL,
            Material.GRAVEL,
            Material.MUD,
            Material.COMPOSTER
    );

    public static final List<Material> COMMON_LOOTS = List.of(
            Material.IRON_NUGGET,
            Material.GOLD_NUGGET,
            Material.COPPER_INGOT,
            Material.COAL,
            Material.FLINT,
            Material.BONE,

            Material.STRING,
            Material.LEATHER,
            Material.FEATHER,
            Material.GUNPOWDER,

            Material.BREAD,
            Material.APPLE,
            Material.CARROT,
            Material.POTATO,

            Material.EMERALD,
            Material.LAPIS_LAZULI,
            Material.REDSTONE,
            Material.QUARTZ
    );

    public static final List<Material> RAT_LOOTS = List.of(
            Material.ROTTEN_FLESH,
            Material.BONE,
            Material.LEATHER,
            Material.STRING
    );

    public static final List<Material> FISH_LOOTS = List.of(
            Material.COD,
            Material.SALMON,
            Material.PUFFERFISH,
            Material.TROPICAL_FISH,
            Material.KELP,
            Material.SEAGRASS
    );

    public static boolean isRawPrey(ItemStack item) {
        return switch (item.getType()) {
            case COD, SALMON, TROPICAL_FISH, PUFFERFISH, RABBIT, CHICKEN, MUTTON, PORKCHOP, BEEF -> true;
            default -> false;
        };
    }

    public static boolean isBadPrey(ItemStack item) {
        return item.getType() == Material.ROTTEN_FLESH || isRawPrey(item) || isMarkedBadPrey(item);
    }

    public static boolean isCursedHerb(ItemStack item) {
        return switch (item.getType()) {
            case WITHER_ROSE, DEAD_BUSH, CHORUS_FLOWER,
                 WARPED_ROOTS, CRIMSON_ROOTS,
                 SPORE_BLOSSOM, TWISTING_VINES,
                 NETHER_SPROUTS, NETHER_WART,
                 WEEPING_VINES, RED_MUSHROOM_BLOCK,
                 BROWN_MUSHROOM_BLOCK, MANGROVE_ROOTS -> true;
            default -> false;
        };
    }

    public static boolean isToxicHerb(ItemStack item) {
        return switch (item.getType()) {
            case DEAD_BUSH, WITHER_ROSE, FERN, LARGE_FERN,
                 WARPED_ROOTS, CRIMSON_ROOTS, NETHER_SPROUTS,
                 SPORE_BLOSSOM, AZALEA_LEAVES, SMALL_DRIPLEAF -> true;
            default -> false;
        };
    }

    public static boolean isToxicItem(ItemStack item) {
        return switch (item.getType()) {
            case POISONOUS_POTATO,
                 RED_MUSHROOM, BROWN_MUSHROOM,
                 PUFFERFISH,
                 SPIDER_EYE, FERMENTED_SPIDER_EYE,
                 SUSPICIOUS_STEW,
                 DRAGON_BREATH -> true;
            default -> false;
        };
    }

    public static boolean isMarkedBadPrey(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null && meta.getPersistentDataContainer().has(META_BAD_PREY_KEY, PersistentDataType.BYTE);
    }

    public static void markAsBadPrey(ItemStack item) {
        if (item == null || item.getType().isAir()) return;
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(META_BAD_PREY_KEY, PersistentDataType.BYTE, (byte) 1);
            item.setItemMeta(meta);
        }
    }

    public static boolean isDrinkable(ItemStack item) {
        if (item == null || item.getType().isAir()) return false;

        Material type = item.getType();

        return switch (type) {
            case POTION, MILK_BUCKET, MUSHROOM_STEW, RABBIT_STEW, BEETROOT_SOUP -> true;
            default -> false;
        };
    }

    public static boolean isActiveSkill(Player player, ItemStack item, Skills skill) {
        return MenuSkillTreePath
                .generateActiveSkillsFor(EventsCore.PLAYERS_CACHE.get(player.getUniqueId()), skill.getBranch())
                .values().stream()
                .anyMatch(activeSkill -> isSameItem(activeSkill, item));
    }

    public static boolean isActiveSkill(Player player, ItemStack item) {
        for (Skills skill : Skills.getActiveSkills()) {
            if (isActiveSkill(player, item, skill)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getActiveSkill(Player player, Skills skill) {
        for (SkillBranches branch : SkillBranches.values()) {
            Optional<ItemStack> activeSkillOpt = MenuSkillTreePath
                    .generateActiveSkillsFor(EventsCore.PLAYERS_CACHE.get(player.getUniqueId()), branch)
                    .entrySet().stream()
                    .filter(e -> MenuSkillTreePath.getSkillByIndex(e.getKey(), branch) == skill)
                    .map(Map.Entry::getValue)
                    .findAny();
            if (activeSkillOpt.isPresent()) {
                return activeSkillOpt.get();
            }
        }
        throw new IllegalArgumentException("Could not find active skill for player and skill : " + player.getName() + ", " + skill);
    }

    public static boolean checkForCooldown(Player player, ItemStack item) {
        return checkForCooldown(player, item, META_COOLDOWN_KEY);
    }

    public static boolean checkForCooldown(Player player, ItemStack item, NamespacedKey key) {
        return getCooldown(player, item, key) == 0;
    }

    public static long getCooldown(Player player, ItemStack item) {
        return getCooldown(player, item, META_COOLDOWN_KEY);
    }

    public static long getCooldown(Player player, ItemStack item, NamespacedKey key) {
        if (!isActiveSkill(player, item)) {
            throw new IllegalArgumentException("Item is not an active skill");
        }
        ItemMeta meta = item.getItemMeta();
        long nextTime = meta.getPersistentDataContainer().getOrDefault(key, PersistentDataType.LONG, 0L);
        long now = System.currentTimeMillis();
        long remainingMillis = nextTime - now;
        return Math.max(0, remainingMillis / 1000);
    }

    public static void setCooldown(Player player, ItemStack item, long cooldown) {
        setCooldown(player, item, cooldown, META_COOLDOWN_KEY);
    }

    public static void setCooldown(Player player, ItemStack item, long cooldown, NamespacedKey key) {
        if (!isActiveSkill(player, item)) {
            throw new IllegalArgumentException("Item is not an active skill");
        }
        ItemMeta meta = item.getItemMeta();
        long nextAvailableTime = System.currentTimeMillis() + (cooldown * 1000);
        meta.getPersistentDataContainer().set(key, PersistentDataType.LONG, nextAvailableTime);
        item.setItemMeta(meta);
    }

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.isEmpty();
    }

    public static boolean isUrbanBlock(Material material) {
        return URBAN_BLOCKS.contains(material);
    }

    public static boolean isTrashBlock(Material material) {
        return TRASH_BLOCKS.contains(material);
    }

    public static List<Material> getAllFoods() {
        return Arrays.stream(Material.values())
                .filter(Material::isEdible)
                .collect(Collectors.toList());
    }

    public static ItemStack getRandomLootFromTrash() {
        final Random random = new Random();

        Material loot = ItemsUtils.COMMON_LOOTS.get(random.nextInt(ItemsUtils.COMMON_LOOTS.size()));

        return new ItemStack(loot, 1);
    }

    public static ItemStack getRandomLootFromFish() {
        final Random random = new Random();

        Material loot = FISH_LOOTS.get(random.nextInt(FISH_LOOTS.size()));

        return new ItemStack(loot, 1 + random.nextInt(3));
    }

    public static byte[] serializeItemStackArray(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return outputStream.toByteArray();
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not serialize item stacks array", e);
        }
        return new byte[0];
    }

    public static ItemStack[] deserializeItemStackArray(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not deserialize item stacks array", e);
        }
        return new ItemStack[0];
    }

    // Checks for similarity without NBT tags
    public static boolean isSameItem(ItemStack a, ItemStack b) {
        if (a == null || b == null || a.getType() != b.getType()) return false;

        ItemMeta metaA = a.getItemMeta();
        ItemMeta metaB = b.getItemMeta();

        if (metaA == null || metaB == null) return false;

        if (!Objects.equals(metaA.getDisplayName(), metaB.getDisplayName())) return false;

        List<String> loreA = metaA.getLore();
        List<String> loreB = metaB.getLore();
        if (loreA == null && loreB == null) return true;
        if (loreA == null || loreB == null) return false;

        return loreA.equals(loreB);
    }
}
