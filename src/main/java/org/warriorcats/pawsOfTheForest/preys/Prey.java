package org.warriorcats.pawsOfTheForest.preys;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.ModelEngine;
import org.bukkit.entity.LivingEntity;
import org.warriorcats.pawsOfTheForest.core.configurations.PreysConf;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.util.List;
import java.util.Optional;

public record Prey(String entityType, double xp, long coins, float fleeDurationSeconds, boolean isHigher, boolean isAquatic, boolean isBad) {

    public static Optional<Prey> fromEntity(LivingEntity entity) {
        String entityType = entity.getType().name().toUpperCase();

        ModeledEntity modeledEntity = ModelEngine.getModeledEntity(entity);
        if (modeledEntity != null) {
            entityType = MobsUtils.getModelName(modeledEntity).toUpperCase();
        }

        Optional<Prey> existingPrey = Optional.empty();
        for (Prey prey : PreysConf.Preys.PREYS) {
            if (prey.entityType().equals(entityType)) {
                existingPrey = Optional.of(prey);
                break;
            }
        }

        return existingPrey;
    }

    public static boolean isPrey(LivingEntity entity) {
        return fromEntity(entity).isPresent();
    }

    public static List<LivingEntity> getAllEntities() {
        return MobsUtils.getAllEntities().stream()
                .filter(Prey::isPrey)
                .toList();
    }

    public static List<Prey> getAllCommons() {
        return PreysConf.Preys.PREYS.stream()
                .filter(prey -> !prey.isHigher)
                .toList();
    }

    public static List<Prey> getAllHighers() {
        return PreysConf.Preys.PREYS.stream()
                .filter(prey -> prey.isHigher)
                .toList();
    }

    public static List<Prey> getAllBadsToEat() {
        return PreysConf.Preys.PREYS.stream()
                .filter(prey -> prey.isBad)
                .toList();
    }

    public static List<Prey> getAllGoodsToEat() {
        return PreysConf.Preys.PREYS.stream()
                .filter(prey -> !prey.isBad)
                .toList();
    }
}
