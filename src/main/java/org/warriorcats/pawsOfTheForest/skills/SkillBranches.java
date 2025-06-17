package org.warriorcats.pawsOfTheForest.skills;

import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

public enum SkillBranches {
    HUNTING, NAVIGATION, RESILIENCE, HERBALIST, KITTYPET, LONER, ROGUE, CITY_CAT;

    @Override
    public String toString() {
        return StringsUtils.capitalize(name().toLowerCase());
    }

    public static final double UNLOCK_SKILL = 8;
    public static final double UNLOCK_SKILL_TIER = 2;
}
