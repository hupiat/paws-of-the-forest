package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.configuration.ConfigurationSection;
import org.warriorcats.pawsOfTheForest.preys.Prey;

import java.util.HashSet;
import java.util.Set;

public abstract class PreysConf extends AbstractConfiguration {

    public static final String CONFIG_FILE_NAME = "preys_config.yaml";

    public static class Preys {
        public static final Set<Prey> PREYS = new HashSet<>();

        static {
            ConfigurationSection preysSource = yamlSource.getConfigurationSection("prey");
            for (var entry : preysSource.getKeys(false)) {
                Prey prey = new Prey(
                        entry.toUpperCase(),
                        preysSource.getDouble(entry + ".xp"),
                        preysSource.getLong(entry + ".coins")
                );
                PREYS.add(prey);
            }
        }
    }
}
