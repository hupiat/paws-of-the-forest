package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.skills.menus.EventsSkillsMenu;
import org.warriorcats.pawsOfTheForest.skills.menus.MenuSkillTreePath;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class ItemsUtils {

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

    public static boolean isActiveSkill(Player player, ItemStack item, Skills skill) {
        return MenuSkillTreePath
                .generateActiveSkillsFor(EventsCore.PLAYERS_CACHE.get(player.getUniqueId()), skill.getBranch())
                .values().stream()
                .anyMatch(activeSkill -> activeSkill.isSimilar(item));
    }

    public static boolean isActiveSkill(Player player, ItemStack item) {
        for (Skills skill : Skills.getActiveSkills()) {
            if (isActiveSkill(player, item, skill)) {
                return true;
            }
        }
        return false;
    }

    public static ItemStack getActiveSkill(Player player, Material icon) {
        for (SkillBranches branch : SkillBranches.values()) {
            Optional<ItemStack> activeSkillOpt = MenuSkillTreePath
                    .generateActiveSkillsFor(EventsCore.PLAYERS_CACHE.get(player.getUniqueId()), branch)
                    .values().stream()
                    .filter(activeSkill -> activeSkill.getType() == icon)
                    .findAny();
            if (activeSkillOpt.isPresent()) {
                return activeSkillOpt.get();
            }
        }
        throw new IllegalArgumentException("Could not find active skill for player and icon : " + player.getName() + ", " + icon);
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
}
