package org.warriorcats.pawsOfTheForest.preys;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.ModelEngine;
import org.bukkit.entity.Entity;
import org.warriorcats.pawsOfTheForest.core.configurations.PreysConf;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.util.Optional;

public record Prey(String entityType, double xp, long coins) {

    public static Optional<Prey> fromEntity(Entity entity) {
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

    public static boolean isPrey(Entity entity) {
        return fromEntity(entity).isPresent();
    }
}
