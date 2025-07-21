package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.configuration.ConfigurationSection;
import org.warriorcats.pawsOfTheForest.preys.Prey;

import java.util.HashSet;
import java.util.Set;

public abstract class PreysConf extends BaseConfiguration {

    public static final String CONFIG_FILE_NAME = "preys_config.yaml";

    @Override
    public void load(String configFileName) {
        super.load(configFileName);
        ConfigurationSection preysSource = yamlSource.getConfigurationSection("prey");
        for (var entry : preysSource.getKeys(false)) {
            Prey prey = new Prey(
                    entry.toUpperCase(),
                    preysSource.getDouble(entry + ".xp"),
                    preysSource.getLong(entry + ".coins"),
                    (float) preysSource.getDouble(entry + ".flee_duration_seconds"),
                    preysSource.getBoolean(entry + ".higher"),
                    preysSource.getBoolean(entry + ".aquatic"),
                    preysSource.getBoolean(entry + ".bad")
            );
            Preys.PREYS.add(prey);
        }
    }

    public static class Preys {
        public static final Set<Prey> PREYS = new HashSet<>();
    }
}
