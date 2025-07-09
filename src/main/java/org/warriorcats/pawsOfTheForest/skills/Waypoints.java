package org.warriorcats.pawsOfTheForest.skills;

import lombok.Getter;
import org.bukkit.block.Biome;
import org.warriorcats.pawsOfTheForest.utils.StringsUtils;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

@Getter
public enum Waypoints {
    CAMP(Set.of(
            Biome.PLAINS,
            Biome.SUNFLOWER_PLAINS,
            Biome.MEADOW,
            Biome.CHERRY_GROVE,
            Biome.BEACH,
            Biome.SNOWY_PLAINS
    )),

    DEN(Set.of(
            Biome.FOREST,
            Biome.BIRCH_FOREST,
            Biome.OLD_GROWTH_BIRCH_FOREST,
            Biome.DARK_FOREST,
            Biome.TAIGA,
            Biome.SNOWY_TAIGA,
            Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.OLD_GROWTH_SPRUCE_TAIGA,
            Biome.WINDSWEPT_FOREST
    )),

    HERB_PATCH(Set.of(
            Biome.FLOWER_FOREST,
            Biome.JUNGLE,
            Biome.BAMBOO_JUNGLE,
            Biome.SPARSE_JUNGLE,
            Biome.SWAMP,
            Biome.MANGROVE_SWAMP,
            Biome.RIVER,
            Biome.FROZEN_RIVER
    )),

    HUNTING_GROUNDS(Set.of(
            Biome.SAVANNA,
            Biome.WINDSWEPT_SAVANNA,
            Biome.WINDSWEPT_HILLS,
            Biome.WINDSWEPT_GRAVELLY_HILLS,
            Biome.BADLANDS,
            Biome.WOODED_BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.DESERT,
            Biome.STONY_PEAKS,
            Biome.GROVE,
            Biome.SNOWY_SLOPES
    ));

    private Set<Biome> biomes;

    Waypoints(Set<Biome> biomes) {
        this.biomes = biomes;
    }

    @Override
    public String toString() {
        return StringsUtils.capitalizeWithSpaces(name(), "_");
    }

    public static Optional<Waypoints> getFromBiome(Biome biome) {
        for (Waypoints value : values()) {
            if (value.getBiomes().contains(biome)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }

    @Nullable public static Waypoints getFromIndex(int index) {
        return switch (index) {
            case 0 -> CAMP;
            case 1 -> DEN;
            case 2 -> HERB_PATCH;
            case 3 -> HUNTING_GROUNDS;
            default -> null;
        };
    }

    public static int getIndex(Waypoints waypoint) {
        return switch (waypoint) {
            case CAMP -> 0;
            case DEN -> 1;
            case HERB_PATCH -> 2;
            case HUNTING_GROUNDS -> 3;
        };
    }

    public static Waypoints from(String waypointStr) {
        for (Waypoints waypoint : values()) {
            if (waypoint.toString().toLowerCase().startsWith(waypointStr.toLowerCase())) {
                return waypoint;
            }
        }
        return valueOf(waypointStr);
    }
}
