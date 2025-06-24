package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.damage.DamageType;

import java.util.Set;

public abstract class BiomesUtils {

    public static final Set<Biome> WATER_BIOMES = Set.of(
            Biome.OCEAN,
            Biome.DEEP_OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.FROZEN_OCEAN,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.LUKEWARM_OCEAN,
            Biome.DEEP_LUKEWARM_OCEAN,
            Biome.WARM_OCEAN,
            Biome.RIVER,
            Biome.FROZEN_RIVER
    );

    public static final Set<Biome> FOREST_BIOMES = Set.of(
            Biome.FOREST,
            Biome.BIRCH_FOREST,
            Biome.OLD_GROWTH_BIRCH_FOREST,
            Biome.DARK_FOREST,
            Biome.FLOWER_FOREST,
            Biome.TAIGA,
            Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.OLD_GROWTH_SPRUCE_TAIGA,
            Biome.SNOWY_TAIGA,
            Biome.WINDSWEPT_FOREST,
            Biome.GROVE,
            Biome.WOODED_BADLANDS
    );

    public static final Set<Biome> PLAINS_BIOMES = Set.of(
            Biome.PLAINS,
            Biome.SUNFLOWER_PLAINS,
            Biome.SAVANNA,
            Biome.SAVANNA_PLATEAU,
            Biome.WINDSWEPT_SAVANNA,
            Biome.MEADOW
    );

    public static final Set<Biome> COLD_BIOMES = Set.of(
            Biome.FROZEN_OCEAN,
            Biome.FROZEN_RIVER,
            Biome.SNOWY_PLAINS,
            Biome.SNOWY_BEACH,
            Biome.SNOWY_TAIGA,
            Biome.ICE_SPIKES,
            Biome.GROVE,
            Biome.SNOWY_SLOPES,
            Biome.FROZEN_PEAKS,
            Biome.JAGGED_PEAKS,
            Biome.DEEP_FROZEN_OCEAN,
            Biome.COLD_OCEAN,
            Biome.DEEP_COLD_OCEAN,
            Biome.TAIGA,
            Biome.OLD_GROWTH_PINE_TAIGA,
            Biome.OLD_GROWTH_SPRUCE_TAIGA
    );

    public static final Set<Biome> HOT_BIOMES = Set.of(
            Biome.DESERT,
            Biome.BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.WOODED_BADLANDS,
            Biome.SAVANNA,
            Biome.SAVANNA_PLATEAU,
            Biome.WINDSWEPT_SAVANNA,
            Biome.JUNGLE,
            Biome.SPARSE_JUNGLE,
            Biome.BAMBOO_JUNGLE,
            Biome.WARM_OCEAN,
            Biome.NETHER_WASTES,
            Biome.SOUL_SAND_VALLEY,
            Biome.CRIMSON_FOREST,
            Biome.WARPED_FOREST,
            Biome.BASALT_DELTAS
    );

    public static final Set<DamageType> FIRE_DAMAGE_TYPES = Set.of(
            DamageType.IN_FIRE,
            DamageType.ON_FIRE,
            DamageType.CAMPFIRE,
            DamageType.LAVA,
            DamageType.HOT_FLOOR,
            DamageType.FIREBALL,
            DamageType.UNATTRIBUTED_FIREBALL
    );

    public static final Set<DamageType> FREEZE_DAMAGE_TYPES = Set.of(
            DamageType.FREEZE
    );

    public static boolean isOpenSpace(Location loc) {
        for (int y = 1; y <= 10; y++) {
            if (!loc.clone().add(0, y, 0).getBlock().isEmpty()) {
                return false;
            }
        }
        return loc.getBlock().getLightFromSky() >= 14;
    }

    public static boolean isWater(Biome biome) {
        return WATER_BIOMES.contains(biome);
    }

    public static boolean isForest(Biome biome) {
        return FOREST_BIOMES.contains(biome);
    }

    public static boolean isPlain(Biome biome) {
        return PLAINS_BIOMES.contains(biome);
    }

    public static boolean isCold(Biome biome) {
        return COLD_BIOMES.contains(biome);
    }

    public static boolean isHot(Biome biome) {
        return HOT_BIOMES.contains(biome);
    }

    public static boolean isNight(World world) {
        long time = world.getTime();
        return time >= 13000 && time <= 23000;
    }

    public static boolean isDark(Location location) {
        return location.getBlock().getLightLevel() < 8;
    }

    public static boolean isDamageFromFire(DamageType damageType) {
        return FIRE_DAMAGE_TYPES.contains(damageType);
    }

    public static boolean isDamageFromFreeze(DamageType damageType) {
        return FREEZE_DAMAGE_TYPES.contains(damageType);
    }
}
