package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.Skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public abstract class PlayersUtils {

    public static void increaseMovementSpeed(Player player, Supplier<Boolean> condition, double factor, float defaultSpeed) {
        if (condition.get()) {
            player.setWalkSpeed((float) (defaultSpeed * (1 + factor)));
        } else {
            player.setWalkSpeed(defaultSpeed);
        }
    }

    public static boolean hasActiveSkillInInventory(Player player, Skills skill) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (ItemsUtils.isActiveSkill(player, item)) {
                ItemStack activeSkill = ItemsUtils.getActiveSkill(player, skill.getIcon());
                if (activeSkill.isSimilar(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasNoteBlockInInventory(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (!ItemsUtils.isEmpty(item) && item.getType() == Material.NOTE_BLOCK) {
                return true;
            }
        }
        return false;
    }

    public static void synchronizeInventory(Player player) {
        synchronizeInventory(player, EventsCore.PLAYERS_CACHE.get(player.getUniqueId()));
    }

    public static void synchronizeInventory(Player player, PlayerEntity entity) {
        List<Skills> skills = Skills.getActiveSkills().stream()
                .filter(entity::hasAbility)
                .toList();

        // First, clearing skills and noteblock
        for (int i = 0; i < player.getInventory().getContents().length; i++) {
            ItemStack itemStack = player.getInventory().getContents()[i];
            if (ItemsUtils.isActiveSkill(player, itemStack) || itemStack.getType() == Material.NOTE_BLOCK) {
                player.getInventory().clear(i);
            }
        }

        // Prepare items to add (skills + noteblock if needed)
        List<ItemStack> itemsToAdd = new ArrayList<>();
        for (Skills skill : skills) {
            if (!hasActiveSkillInInventory(player, skill)) {
                itemsToAdd.add(ItemsUtils.getActiveSkill(player, skill.getIcon()));
            }
        }
        boolean needNoteBlock = !hasNoteBlockInInventory(player);
        if (needNoteBlock) {
            itemsToAdd.add(new ItemStack(Material.NOTE_BLOCK));
        }

        int neededSlots = itemsToAdd.size();

        // Computing the number of free slots
        ItemStack[] contents = player.getInventory().getContents();
        int freeSlots = (int) Arrays.stream(contents)
                .filter(ItemsUtils::isEmpty)
                .count();

        // If we're missing place, dropping the needed items
        if (freeSlots < neededSlots) {
            int toDrop = neededSlots - freeSlots;

            for (int i = 0; i < contents.length && toDrop > 0; i++) {
                ItemStack item = contents[i];
                if (ItemsUtils.isEmpty(item)) continue;
                if (ItemsUtils.isActiveSkill(player, item)) continue;
                if (item.getType() == Material.NOTE_BLOCK) continue;

                player.getWorld().dropItemNaturally(player.getLocation(), item);
                player.getInventory().clear(i);
                toDrop--;
            }
        }

        // Finally, adding skills and noteblock if needed
        for (ItemStack itemStack : itemsToAdd) {
            player.getInventory().addItem(itemStack);
        }

        player.updateInventory();
    }
}
