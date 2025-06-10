package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;

@Getter
public enum Skills {
    PREY_SENSE("Prey Sense", true, SkillBranches.HUNTING),
    HUNTERS_COMPASS("Hunter’s Compass", true, SkillBranches.HUNTING),
    LOW_SWEEP("Low Sweep", true, SkillBranches.HUNTING),
    SILENT_PAW("Silent Paw", false, SkillBranches.HUNTING),
    BLOOD_HUNTER("Blood Hunter", false, SkillBranches.HUNTING),
    EFFICIENT_KILL("Efficient Kill", false, SkillBranches.HUNTING),

    LOCATION_AWARENESS("Location Awareness", true, SkillBranches.NAVIGATION),
    PATHFINDING_BOOST("Pathfinding Boost", true, SkillBranches.NAVIGATION),
    TRAIL_MEMORY("Trail Memory", false, SkillBranches.NAVIGATION),
    ENDURANCE_TRAVELER("Endurance Traveler", false, SkillBranches.NAVIGATION),
    CLIMBERS_GRACE("Climber’s Grace", false, SkillBranches.NAVIGATION),

    HOLD_ON("Hold On!", true, SkillBranches.RESILIENCE),
    ON_YOUR_PAWS("On Your Paws!", true, SkillBranches.RESILIENCE),
    IRON_HIDE("Iron Hide", false, SkillBranches.RESILIENCE),
    IMMUNE_SYSTEM("Immune System", false, SkillBranches.RESILIENCE),
    THICK_COAT("Thick Coat", false, SkillBranches.RESILIENCE),

    HERB_KNOWLEDGE("Herb Knowledge", true, SkillBranches.HERBALIST),
    BREW_REMEDY("Brew Remedy", true, SkillBranches.HERBALIST),
    QUICK_GATHERER("Quick Gatherer", false, SkillBranches.HERBALIST),
    BOTANICAL_LORE("Botanical Lore", false, SkillBranches.HERBALIST),
    CLEAN_PAWS("Clean Paws", false, SkillBranches.HERBALIST);

    private final String displayName;
    private final boolean isActive;
    private final SkillBranches branche;

    Skills(String displayName, boolean isActive, SkillBranches branche) {
        this.displayName = displayName;
        this.isActive = isActive;
        this.branche = branche;
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
