package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public enum Skills {
    PREY_SENSE("Prey Sense", true, SkillBranches.HUNTING, 1, Material.GHAST_TEAR),
    HUNTERS_COMPASS("Hunter’s Compass", true, SkillBranches.HUNTING, 1, Material.COMPASS),
    LOW_SWEEP("Low Sweep", true, SkillBranches.HUNTING, 1, Material.RABBIT_FOOT),
    SILENT_PAW("Silent Paw", false, SkillBranches.HUNTING, 3, Material.LEATHER),
    BLOOD_HUNTER("Blood Hunter", false, SkillBranches.HUNTING, 4, Material.REDSTONE),
    EFFICIENT_KILL("Efficient Kill", false, SkillBranches.HUNTING, 3, Material.COOKED_BEEF),

    LOCATION_AWARENESS("Location Awareness", true, SkillBranches.NAVIGATION, 1, Material.COMPASS),
    PATHFINDING_BOOST("Pathfinding Boost", true, SkillBranches.NAVIGATION, 1, Material.FEATHER),
    TRAIL_MEMORY("Trail Memory", false, SkillBranches.NAVIGATION, 3, Material.FILLED_MAP),
    ENDURANCE_TRAVELER("Endurance Traveler", false, SkillBranches.NAVIGATION, 4, Material.COOKED_PORKCHOP),
    CLIMBERS_GRACE("Climber’s Grace", false, SkillBranches.NAVIGATION, 2, Material.LADDER),

    HOLD_ON("Hold On!", true, SkillBranches.RESILIENCE, 1, Material.TOTEM_OF_UNDYING),
    ON_YOUR_PAWS("On Your Paws!", true, SkillBranches.RESILIENCE, 1, Material.GOLDEN_APPLE),
    IRON_HIDE("Iron Hide", false, SkillBranches.RESILIENCE, 3, Material.IRON_CHESTPLATE),
    IMMUNE_SYSTEM("Immune System", false, SkillBranches.RESILIENCE, 3, Material.SPIDER_EYE),
    THICK_COAT("Thick Coat", false, SkillBranches.RESILIENCE, 2, Material.SNOWBALL),
    HEARTY_APPETITE("Hearty Appetite", false, SkillBranches.RESILIENCE, 3, Material.COOKED_MUTTON),
    BEAST_OF_BURDEN("Beast of Burden", false, SkillBranches.RESILIENCE, 2, Material.CHEST),

    HERB_KNOWLEDGE("Herb Knowledge", true, SkillBranches.HERBALIST, 1, Material.FERN),
    BREW_REMEDY("Brew Remedy", true, SkillBranches.HERBALIST, 1, Material.BREWING_STAND),
    QUICK_GATHERER("Quick Gatherer", false, SkillBranches.HERBALIST, 3, Material.SHEARS),
    BOTANICAL_LORE("Botanical Lore", false, SkillBranches.HERBALIST, 3, Material.WRITABLE_BOOK),
    CLEAN_PAWS("Clean Paws", false, SkillBranches.HERBALIST, 2, Material.HONEYCOMB),

    WELL_FED("Well-Fed", false, SkillBranches.KITTYPET, 1, Material.COOKED_SALMON),
    PAMPERED("Pampered", false, SkillBranches.KITTYPET, 1, Material.MILK_BUCKET),
    SHELTERED_MIND("Sheltered Mind", false, SkillBranches.KITTYPET, 1, Material.BOOK),

    TRACKER("Tracker", false, SkillBranches.LONER, 1, Material.COMPASS),
    CRAFTY("Crafty", false, SkillBranches.LONER, 1, Material.FERN),
    FLEXIBLE_MORALS("Flexible Morals", false, SkillBranches.LONER, 1, Material.EMERALD),

    AMBUSHER("Ambusher", false, SkillBranches.ROGUE, 1, Material.IRON_SWORD),
    SCAVENGE("Scavenge", false, SkillBranches.ROGUE, 1, Material.ROTTEN_FLESH),
    HARD_KNOCK_LIFE("Hard Knock Life", false, SkillBranches.ROGUE, 1, Material.LEATHER_CHESTPLATE),

    URBAN_NAVIGATION("Urban Navigation", false, SkillBranches.CITY_CAT, 1, Material.STONE),
    RAT_CATCHER("Rat Catcher", false, SkillBranches.CITY_CAT, 1, Material.RABBIT),
    DISEASE_RESISTANCE("Disease Resistance", false, SkillBranches.CITY_CAT, 1, Material.SPIDER_EYE),

    SPEED_OF_THE_MOOR("Speed of the Moor", false, SkillBranches.BREEZE_CLAN, 1, Material.SUGAR),
    LIGHTSTEP("Lightstep", false, SkillBranches.BREEZE_CLAN, 1, Material.FEATHER),
    SHARP_WIND("Sharp Wind", false, SkillBranches.BREEZE_CLAN, 1, Material.PAPER),

    THICK_PELT("Thick Pelt", false, SkillBranches.ECHO_CLAN, 1, Material.LEATHER),
    FOREST_COVER("Forest Cover", false, SkillBranches.ECHO_CLAN, 1, Material.OAK_LEAVES),
    STUNNING_BLOW("Stunning Blow", false, SkillBranches.ECHO_CLAN, 1, Material.STONE_AXE),

    STRONG_SWIMMER("Strong Swimmer", false, SkillBranches.CREEK_CLAN, 1, Material.KELP),
    AQUA_BALANCE("Aqua Balance", false, SkillBranches.CREEK_CLAN, 1, Material.FISHING_ROD),
    WATERS_RESILIENCE("Water’s Resilience", false, SkillBranches.CREEK_CLAN, 1, Material.TURTLE_HELMET),

    NIGHTSTALKER("Nightstalker", false, SkillBranches.SHADE_CLAN, 1, Material.ENDER_PEARL),
    TOXIC_CLAWS("Toxic Claws", false, SkillBranches.SHADE_CLAN, 1, Material.POISONOUS_POTATO),
    SILENT_KILL("Silent Kill", false, SkillBranches.SHADE_CLAN, 1, Material.IRON_SWORD);

    private final String displayName;
    private final boolean isActive;
    private final SkillBranches branch;
    private final int maxTiers;
    private final Material icon;

    Skills(String displayName, boolean isActive, SkillBranches branch, int maxTiers, Material icon) {
        if (isActive && maxTiers != 1) {
            throw new IllegalArgumentException("An active skill must have only 1 max tier");
        }
        this.displayName = displayName;
        this.isActive = isActive;
        this.branch = branch;
        this.maxTiers = maxTiers;
        this.icon = icon;
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

    public static Set<Skills> getActiveSkills() {
        return Arrays.stream(values()).filter(Skills::isActive).collect(Collectors.toSet());
    }
}
