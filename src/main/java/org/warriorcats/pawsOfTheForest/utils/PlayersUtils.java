package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsActives;
import org.warriorcats.pawsOfTheForest.skills.Skills;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class PlayersUtils {

    private static final String META_DOWNED_KEY = "downed";
    private static final String META_DOWNED_CD_KEY = "hold_on_cd";
    private static final String META_WAYPOINT_INDEX = "waypoint_index";

    public static Optional<Player> getNearestPlayer(Player source) {
        Location sourceLoc = source.getLocation();
        Player nearest = null;
        double minDistanceSquared = Double.MAX_VALUE;

        for (Player target : Bukkit.getOnlinePlayers()) {
            if (target.equals(source) || !target.getWorld().equals(source.getWorld())) continue;

            double distanceSquared = sourceLoc.distanceSquared(target.getLocation());
            if (distanceSquared < minDistanceSquared) {
                minDistanceSquared = distanceSquared;
                nearest = target;
            }
        }

        return Optional.ofNullable(nearest);
    }

    public static int getWaypointIndex(Player player) {
        return player.hasMetadata(META_WAYPOINT_INDEX)
                ? player.getMetadata(META_WAYPOINT_INDEX).getFirst().asInt()
                : -1;
    }

    public static void setWaypointIndex(Player player, int index) {
        player.setMetadata(META_WAYPOINT_INDEX,
                new FixedMetadataValue(PawsOfTheForest.getInstance(), index));
    }

    public static boolean isDowned(Player player) {
        return player.hasMetadata(META_DOWNED_KEY) && player.getMetadata(META_DOWNED_KEY).getFirst().asBoolean();
    }

    public static void setDowned(Player player, boolean state) {
        if (state) {
            player.setMetadata(META_DOWNED_KEY, new FixedMetadataValue(PawsOfTheForest.getInstance(), true));
        } else {
            player.removeMetadata(META_DOWNED_KEY, PawsOfTheForest.getInstance());
        }
    }

    public static long getDownedCooldown(Player player) {
        if (!player.hasMetadata(META_DOWNED_CD_KEY)) return 0;

        long remaining = player.getMetadata(META_DOWNED_CD_KEY).getFirst().asLong() - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }

    public static boolean hasHoldOnOnCooldown(Player player) {
        return player.hasMetadata(META_DOWNED_CD_KEY) &&
                player.getMetadata(META_DOWNED_CD_KEY).getFirst().asLong() > System.currentTimeMillis();
    }

    public static void markHoldOnUsed(Player player) {
        long until = System.currentTimeMillis() + (EventsSkillsActives.HOLD_ON_COOLDOWN_S * 1000);
        player.setMetadata(META_DOWNED_CD_KEY, new FixedMetadataValue(PawsOfTheForest.getInstance(), until));
    }

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
                ItemStack activeSkill = ItemsUtils.getActiveSkill(player, skill);
                if (ItemsUtils.isSameItem(activeSkill, item)) {
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
            if (!ItemsUtils.isEmpty(itemStack) &&
                    (ItemsUtils.isActiveSkill(player, itemStack) || itemStack.getType() == Material.NOTE_BLOCK)) {
                player.getInventory().clear(i);
            }
        }

        // Prepare items to add (skills + noteblock if needed)
        List<ItemStack> itemsToAdd = new ArrayList<>();
        for (Skills skill : skills) {
            if (!hasActiveSkillInInventory(player, skill)) {
                itemsToAdd.add(ItemsUtils.getActiveSkill(player, skill));
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
