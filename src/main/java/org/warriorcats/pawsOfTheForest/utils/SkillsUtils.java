package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SkillsUtils {

    private static final String NAME_IRON_HIDE_BONUS = "IRON_HIDE_BONUS";
    private static final UUID UUID_IRON_HIDE_BONUS = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private static final String NAME_HARD_KNOCK_LIFE_BONUS = "HARD_KNOCK_LIFE_BONUS";
    private static final UUID UUID_HARD_KNOCK_LIFE_BONUS = UUID.fromString("00000000-0000-0000-0000-000000000002");

    public static void updateIronHideArmor(Player player, int tier) {
        var attr = player.getAttribute(Attribute.GENERIC_ARMOR);

        removeModifiers(UUID_IRON_HIDE_BONUS, NAME_IRON_HIDE_BONUS, attr);

        if (tier > 0) {
            AttributeModifier mod = new AttributeModifier(
                    UUID_IRON_HIDE_BONUS,
                    NAME_IRON_HIDE_BONUS,
                    tier,
                    AttributeModifier.Operation.ADD_NUMBER
            );
            attr.addModifier(mod);
        }
    }

    public static void updateHardKnockLifeArmor(Player player) {
        var attr = player.getAttribute(Attribute.GENERIC_ARMOR);

        removeModifiers(UUID_HARD_KNOCK_LIFE_BONUS, NAME_HARD_KNOCK_LIFE_BONUS, attr);

        AttributeModifier mod = new AttributeModifier(
                UUID_HARD_KNOCK_LIFE_BONUS,
                NAME_HARD_KNOCK_LIFE_BONUS,
                1,
                AttributeModifier.Operation.ADD_NUMBER
        );
        attr.addModifier(mod);
    }

    private static void removeModifiers(UUID uuid, String name, AttributeInstance attr) {
        var toRemove = attr.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(uuid)
                        || mod.getName().equals(name))
                .toList();
        toRemove.forEach(attr::removeModifier);
    }
}
