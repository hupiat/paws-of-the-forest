package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.block.Biome;
import org.bukkit.damage.DamageType;

import java.util.Set;

public abstract class BiomesUtils {

    private static final Set<Biome> COLD_BIOMES = Set.of(
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

    private static final Set<Biome> HOT_BIOMES = Set.of(
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

    private static final Set<DamageType> FIRE_DAMAGE_TYPES = Set.of(
            DamageType.IN_FIRE,
            DamageType.ON_FIRE,
            DamageType.CAMPFIRE,
            DamageType.LAVA,
            DamageType.HOT_FLOOR,
            DamageType.FIREBALL,
            DamageType.UNATTRIBUTED_FIREBALL
    );

    private static final Set<DamageType> FREEZE_DAMAGE_TYPES = Set.of(
            DamageType.FREEZE
    );

    public static boolean isCold(Biome biome) {
        return COLD_BIOMES.contains(biome);
    }

    public static boolean isHot(Biome biome) {
        return HOT_BIOMES.contains(biome);
    }

    public static boolean isDamageFromFire(DamageType damageType) {
        return FIRE_DAMAGE_TYPES.contains(damageType);
    }

    public static boolean isDamageFromFreeze(DamageType damageType) {
        return FREEZE_DAMAGE_TYPES.contains(damageType);
    }
}
