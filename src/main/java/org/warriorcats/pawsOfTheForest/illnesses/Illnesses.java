package org.warriorcats.pawsOfTheForest.illnesses;

import lombok.Getter;
import org.bukkit.potion.PotionEffectType;
import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

import java.util.Map;

@Getter
public enum Illnesses {
    UPPER_RESPIRATORY_INFECTION(false, 5, Map.of(
            PotionEffectType.SLOWNESS, 0,
            PotionEffectType.WEAKNESS, 0,
            PotionEffectType.HUNGER, 0,
            PotionEffectType.NAUSEA, 0
    )),

    RABIES(true, 2, Map.of(
            PotionEffectType.NAUSEA, 2,
            PotionEffectType.BLINDNESS, 0,
            PotionEffectType.JUMP_BOOST, 1
    )),

    INTERNAL_PARASITES(true, 7, Map.of(
            PotionEffectType.HUNGER, 1,
            PotionEffectType.SLOWNESS, 0
    )),

    EXTERNAL_PARASITES(false, 0, Map.of(
            PotionEffectType.SLOWNESS, 0,
            PotionEffectType.HUNGER, 0,
            PotionEffectType.NAUSEA, 0
    )),

    FROSTBITE(true, 2, Map.of(
            PotionEffectType.SLOWNESS, 1,
            PotionEffectType.WITHER, 0,
            PotionEffectType.BLINDNESS, 0
    )),

    HEATSTROKE(true, 2, Map.of(
            PotionEffectType.NAUSEA, 1,
            PotionEffectType.BLINDNESS, 0,
            PotionEffectType.WEAKNESS, 0
    )),

    INFECTED_WOUNDS(true, 3, Map.of(
            PotionEffectType.WITHER, 0,
            PotionEffectType.WEAKNESS, 1
    )),

    BROKEN_BONES(false, 0, Map.of(
            PotionEffectType.SLOWNESS, 3,
            PotionEffectType.NAUSEA, 0
    )),

    POISONING(true, 1, Map.of(
            PotionEffectType.POISON, 1,
            PotionEffectType.NAUSEA, 0,
            PotionEffectType.HUNGER, 0,
            PotionEffectType.WITHER, 0
    )),

    SEIZURES(false, 0, Map.of(
            PotionEffectType.NAUSEA, 0,
            PotionEffectType.BLINDNESS, 0,
            PotionEffectType.LEVITATION, 0
    )),

    ARTHRITIS(false, 0, Map.of(
            PotionEffectType.SLOWNESS, 0
    ));

    private final boolean fatal;
    private final int daysBeforeWorsened;
    private final Map<PotionEffectType, Integer> potionEffects;

    Illnesses(boolean fatal, int daysBeforeWorsened, Map<PotionEffectType, Integer> potionEffects) {
        this.fatal = fatal;
        this.daysBeforeWorsened = daysBeforeWorsened;
        this.potionEffects = potionEffects;
    }

    @Override
    public String toString() {
        return StringsUtils.capitalizeWithSpaces(name(), "_");
    }

    public static Illnesses from(String illnessStr) {
        return EnumsUtils.from(illnessStr, Illnesses.class);
    }
}
