package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;

@Getter
public enum Skills {
    PREY_SENSE("Prey Sense", true, SkillBranches.HUNTING, 1),
    HUNTERS_COMPASS("Hunter’s Compass", true, SkillBranches.HUNTING, 1),
    LOW_SWEEP("Low Sweep", true, SkillBranches.HUNTING, 1),
    SILENT_PAW("Silent Paw", false, SkillBranches.HUNTING, 3),
    BLOOD_HUNTER("Blood Hunter", false, SkillBranches.HUNTING, 4),
    EFFICIENT_KILL("Efficient Kill", false, SkillBranches.HUNTING, 3),

    LOCATION_AWARENESS("Location Awareness", true, SkillBranches.NAVIGATION, 1),
    PATHFINDING_BOOST("Pathfinding Boost", true, SkillBranches.NAVIGATION, 1),
    TRAIL_MEMORY("Trail Memory", false, SkillBranches.NAVIGATION, 3),
    ENDURANCE_TRAVELER("Endurance Traveler", false, SkillBranches.NAVIGATION, 4),
    CLIMBERS_GRACE("Climber’s Grace", false, SkillBranches.NAVIGATION, 2),

    HOLD_ON("Hold On!", true, SkillBranches.RESILIENCE, 1),
    ON_YOUR_PAWS("On Your Paws!", true, SkillBranches.RESILIENCE, 1),
    IRON_HIDE("Iron Hide", false, SkillBranches.RESILIENCE, 3),
    IMMUNE_SYSTEM("Immune System", false, SkillBranches.RESILIENCE, 3),
    THICK_COAT("Thick Coat", false, SkillBranches.RESILIENCE, 2),
    HEARTY_APPETITE("Hearty Appetite", false, SkillBranches.RESILIENCE, 3),
    BEAST_OF_BURDEN("Beast of Burden", false, SkillBranches.RESILIENCE, 2),

    HERB_KNOWLEDGE("Herb Knowledge", true, SkillBranches.HERBALIST, 1),
    BREW_REMEDY("Brew Remedy", true, SkillBranches.HERBALIST, 1),
    QUICK_GATHERER("Quick Gatherer", false, SkillBranches.HERBALIST, 3),
    BOTANICAL_LORE("Botanical Lore", false, SkillBranches.HERBALIST, 3),
    CLEAN_PAWS("Clean Paws", false, SkillBranches.HERBALIST, 2);

    private final String displayName;
    private final boolean isActive;
    private final SkillBranches branch;
    private final int maxTiers;

    Skills(String displayName, boolean isActive, SkillBranches branch, int maxTiers) {
        if (isActive && maxTiers != 1) {
            throw new IllegalArgumentException("An active skill must have only 1 max tier");
        }

        this.displayName = displayName;
        this.isActive = isActive;
        this.branch = branch;
        this.maxTiers = maxTiers;
    }

    public int getCurrentTier(double xp) {
        if (isActive) {
            return maxTiers;
        }
        return (int) Math.round(xp / SkillBranches.UNLOCK_SKILL_TIER);
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static Skills from(String skillsStr) {
        for (Skills value : values()) {
            if (value.toString().trim().equalsIgnoreCase(skillsStr.trim())) {
                return value;
            }
        }
        return valueOf(skillsStr);
    }
}
