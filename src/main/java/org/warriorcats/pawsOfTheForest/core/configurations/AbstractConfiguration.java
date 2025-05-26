package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;

import java.util.Properties;

public abstract class AbstractConfiguration {

    protected static Properties propertiesSource = new Properties();

    protected static YamlConfiguration yamlSource = new YamlConfiguration();

    public static void load(String configFilePath) {
        if (FileUtils.isYaml(configFilePath)) {
            loadYamlSource(configFilePath);
        } else {
            loadPropertiesSource(configFilePath);
        }
    }

    protected static void loadPropertiesSource(String configFilePath) {
        propertiesSource = FileUtils.load(configFilePath, propertiesSource);
    }

    protected static void loadYamlSource(String configFilePath) {
        yamlSource = FileUtils.load(configFilePath, yamlSource);
    }

    protected static boolean checkForDefaultKey(String key, String defaultValue, String configFilePath) {
        if (FileUtils.isYaml(configFilePath)) {
            if (!yamlSource.contains(key)) {
                yamlSource.set(key, defaultValue);
                FileUtils.store(configFilePath, yamlSource);
                return false;
            }
        } else {
            if (!propertiesSource.containsKey(key)) {
                propertiesSource.setProperty(key, defaultValue);
                FileUtils.store(configFilePath, propertiesSource);
                return false;
            }
        }
        return true;
    }

    protected static String getPropertyOrDefault(String key, String defaultValue, String configFilePath) {
        if (!checkForDefaultKey(key, defaultValue, configFilePath)) {
            return defaultValue;
        }
        return !FileUtils.isYaml(configFilePath) ? propertiesSource.getProperty(key) : (String) yamlSource.get(key);
    }

    protected static ChatColor getPropertyOrDefault(String key, ChatColor defaultValue, String configFilePath) {
        String value = defaultValue.name();
        if (!checkForDefaultKey(key, value, configFilePath)) {
            return defaultValue;
        }
        return ChatColor.valueOf(!FileUtils.isYaml(configFilePath) ?
                propertiesSource.getProperty(key).toUpperCase() :
                ((String) yamlSource.get(key)).toUpperCase());
    }
}
