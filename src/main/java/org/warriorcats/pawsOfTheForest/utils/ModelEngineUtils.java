package org.warriorcats.pawsOfTheForest.utils;

import com.ticxo.modelengine.api.model.ModeledEntity;

public abstract class ModelEngineUtils {

    public static String getModelName(ModeledEntity modeledEntity) {
         return modeledEntity.getModels().entrySet().iterator().next().getKey();
    }
}
