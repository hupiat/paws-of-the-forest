package org.warriorcats.pawsOfTheForest.utils;

import com.ticxo.modelengine.api.model.ModeledEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;

public abstract class MobsUtils {

    public static String getModelName(ModeledEntity modeledEntity) {
         return modeledEntity.getModels().entrySet().iterator().next().getKey();
    }

    public static ActiveMob spawn(Location location, String modelName, double level) {
        MythicMob mob = MythicBukkit.inst().getMobManager().getMythicMob(modelName).orElse(null);

        if (mob == null) {
            throw new IllegalArgumentException("Could not find MythicMob from model name : " + modelName);
        }

        return mob.spawn(BukkitAdapter.adapt(location), level);
    }
}
