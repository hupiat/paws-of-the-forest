package org.warriorcats.pawsOfTheForest.illnesses;

import lombok.Getter;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

import java.util.Set;

@Getter
public enum Illnesses {
    UPPER_RESPIRATORY_INFECTION(false, Set.of(
            PotionEffectType.SLOWNESS,
            PotionEffectType.WEAKNESS,
            PotionEffectType.HUNGER,
            PotionEffectType.NAUSEA
    )),
    RABIES(true, Set.of(
            PotionEffectType.NAUSEA,
            PotionEffectType.JUMP_BOOST
    )),

    INTERNAL_PARASITES(true, Set.of(
            PotionEffectType.HUNGER,
            PotionEffectType.SLOWNESS
    )),
    EXTERNAL_PARASITES(false, Set.of(
            PotionEffectType.SLOWNESS,
            PotionEffectType.HUNGER,
            PotionEffectType.NAUSEA
    )),

    FROSTBITE(true, Set.of(
            PotionEffectType.SLOWNESS,
            PotionEffectType.WITHER,
            PotionEffectType.BLINDNESS
    )),
    HEATSTROKE(true, Set.of(
            PotionEffectType.NAUSEA,
            PotionEffectType.BLINDNESS,
            PotionEffectType.WEAKNESS
    )),

    INFECTED_WOUNDS(true, Set.of(
            PotionEffectType.WITHER,
            PotionEffectType.WEAKNESS
    )),
    BROKEN_BONES(false, Set.of(
            PotionEffectType.SLOWNESS,
            PotionEffectType.NAUSEA
    )),

    POISONING(true, Set.of(
            PotionEffectType.POISON,
            PotionEffectType.NAUSEA,
            PotionEffectType.HUNGER,
            PotionEffectType.WITHER
    )),
    SEIZURES(false, Set.of(
            PotionEffectType.NAUSEA,
            PotionEffectType.LEVITATION
    )),
    ARTHRITIS(false, Set.of(
            PotionEffectType.SLOWNESS
    ));

    private final boolean fatal;
    private final Set<PotionEffectType> potionEffects;

    Illnesses(boolean fatal, Set<PotionEffectType> potionEffects) {
        this.fatal = fatal;
        this.potionEffects = potionEffects;
    }

    @Override
    public String toString() {
        return StringsUtils.capitalizeWithSpaces(name(), "_");
    }

    public static Illnesses from(String illnessStr) {
        for (Illnesses illness : values()) {
            if (illness.toString().toLowerCase().startsWith(illnessStr.toLowerCase())) {
                return illness;
            }
        }
        return valueOf(illnessStr);
    }
}
