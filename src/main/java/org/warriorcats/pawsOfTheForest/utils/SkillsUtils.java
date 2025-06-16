package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class SkillsUtils {

    private static final String NAME_IRON_HIDE_BONUS = "IRON_HIDE_BONUS";
    private static final UUID UUID_IRON_HIDE_BONUS = UUID.fromString("00000000-0000-0000-0000-000000000001");

    public static void updateIronHideArmor(Player player, int tier) {
        var attr = player.getAttribute(Attribute.GENERIC_ARMOR);

        var toRemove = attr.getModifiers().stream()
                .filter(mod -> mod.getUniqueId().equals(UUID_IRON_HIDE_BONUS)
                        || mod.getName().equals(NAME_IRON_HIDE_BONUS))
                .toList();
        toRemove.forEach(attr::removeModifier);

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
}
