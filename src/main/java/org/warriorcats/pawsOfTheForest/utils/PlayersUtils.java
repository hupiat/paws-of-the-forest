package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.skills.Skills;

import java.util.function.Supplier;

public abstract class PlayersUtils {

    public static void increaseMovementSpeed(Player player, Supplier<Boolean> condition, double factor, float defaultSpeed) {
        if (condition.get()) {
            player.setWalkSpeed((float) (defaultSpeed * (1 + factor)));
        } else {
            player.setWalkSpeed(defaultSpeed);
        }
    }
}
