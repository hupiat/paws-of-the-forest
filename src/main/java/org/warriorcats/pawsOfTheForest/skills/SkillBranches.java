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

    static final double UNLOCK_SKILL = 8;
    static final double UNLOCK_SKILL_TIER = 2;
    static final int MAX_TIER = 4;
}
