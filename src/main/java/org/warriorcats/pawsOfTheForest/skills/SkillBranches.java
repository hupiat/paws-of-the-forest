package org.warriorcats.pawsOfTheForest.skills;

import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

public enum SkillBranches {
    HUNTING,
    NAVIGATION,
    RESILIENCE,
    HERBALIST,
    KITTYPET,
    LONER,
    ROGUE,
    CITY_CAT,
    BREEZE_CLAN,
    ECHO_CLAN,
    CREEK_CLAN,
    SHADE_CLAN;

    @Override
    public String toString() {
        return StringsUtils.capitalizeWithSpaces(name(), "_");
    }

    public static final double UNLOCK_SKILL = 8;
    public static final double UNLOCK_SKILL_TIER = 2;

    public static SkillBranches from(String branchStr) {
        for (SkillBranches branche : values()) {
            if (branche.toString().toLowerCase().startsWith(branchStr.toLowerCase())) {
                return branche;
            }
        }
        return valueOf(branchStr);
    }
}
