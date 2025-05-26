package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;

import java.util.Properties;

public abstract class AbstractConfiguration {

    protected static Properties propertiesSource = null;

    protected static YamlConfiguration yamlSource = null;

    protected static void loadPropertiesSource(String configFilePath) {
        propertiesSource = FileUtils.load(configFilePath, propertiesSource);
    }

    protected static void loadYamlSource(String configFilePath) {
        yamlSource = FileUtils.load(configFilePath, yamlSource);
    }

    protected static boolean checkForDefaultKey(String key, String defaultValue, String configFilePath) {
        if (propertiesSource != null) {
            if (!propertiesSource.containsKey(key)) {
                propertiesSource.setProperty(key, defaultValue);
                FileUtils.store(configFilePath, propertiesSource);
                return false;
            }
        } else {
            if (!yamlSource.contains(key)) {
                yamlSource.set(key, defaultValue);
                FileUtils.store(configFilePath, yamlSource);
                return false;
            }
        }
        return true;
    }

    protected static String getPropertyOrDefault(String key, String defaultValue, String configFilePath) {
        if (!checkForDefaultKey(key, defaultValue, configFilePath)) {
            return defaultValue;
        }
        return propertiesSource != null ? propertiesSource.getProperty(key) : (String) yamlSource.get(key);
    }

    protected static ChatColor getPropertyOrDefault(String key, ChatColor defaultValue, String configFilePath) {
        String value = defaultValue.name();
        if (!checkForDefaultKey(key, value, configFilePath)) {
            return defaultValue;
        }
        return ChatColor.valueOf(propertiesSource != null ?
                propertiesSource.getProperty(key).toUpperCase() :
                ((String) yamlSource.get(key)).toUpperCase());
    }
}
