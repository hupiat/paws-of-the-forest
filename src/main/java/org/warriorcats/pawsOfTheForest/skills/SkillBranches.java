package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;
import org.warriorcats.pawsOfTheForest.utils.StringUtils;

@Getter
public enum SkillBranches {
    HUNTING, NAVIGATION, RESILIENCE, HERBALIST;

    @Override
    public String toString() {
        return StringUtils.capitalize(name().toLowerCase());
    }

    public static final double UNLOCK_SKILL = 8;
    public static final double UNLOCK_SKILL_TIER = 2;
}
