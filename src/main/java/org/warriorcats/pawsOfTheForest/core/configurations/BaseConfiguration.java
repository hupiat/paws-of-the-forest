package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;

import java.util.Properties;

public class BaseConfiguration {

    private static BaseConfiguration INSTANCE = null;

    protected Properties propertiesSource = new Properties();

    protected YamlConfiguration yamlSource = new YamlConfiguration();

    protected BaseConfiguration() {
        INSTANCE = this;
    }

    public static BaseConfiguration getInstance() {
        if (INSTANCE == null) {
            new BaseConfiguration();
        }
        return INSTANCE;
    }

    public void load(String configFilePath) {
        if (FileUtils.isYaml(configFilePath)) {
            loadYamlSource(configFilePath);
        } else {
            loadPropertiesSource(configFilePath);
        }
    }

    protected void loadPropertiesSource(String configFilePath) {
        propertiesSource = FileUtils.load(configFilePath, propertiesSource);
    }

    protected void loadYamlSource(String configFilePath) {
        yamlSource = FileUtils.load(configFilePath, yamlSource);
    }

    protected static boolean checkForDefaultKey(String key, String defaultValue, String configFilePath) {
        if (FileUtils.isYaml(configFilePath)) {
            if (!getInstance().yamlSource.contains(key)) {
                getInstance().yamlSource.set(key, defaultValue);
                FileUtils.store(configFilePath, getInstance().yamlSource);
                return false;
            }
        } else {
            if (!getInstance().propertiesSource.containsKey(key)) {
                getInstance().propertiesSource.setProperty(key, defaultValue);
                FileUtils.store(configFilePath, getInstance().propertiesSource);
                return false;
            }
        }
        return true;
    }

    protected static String getPropertyOrDefault(String key, String defaultValue, String configFilePath) {
        if (!checkForDefaultKey(key, defaultValue, configFilePath)) {
            return defaultValue;
        }
        return !FileUtils.isYaml(configFilePath) ? getInstance().propertiesSource.getProperty(key) : (String) getInstance().yamlSource.get(key);
    }

    protected static ChatColor getPropertyOrDefault(String key, ChatColor defaultValue, String configFilePath) {
        String value = defaultValue.name();
        if (!checkForDefaultKey(key, value, configFilePath)) {
            return defaultValue;
        }
        return ChatColor.valueOf(!FileUtils.isYaml(configFilePath) ?
                getInstance().propertiesSource.getProperty(key).toUpperCase() :
                ((String) getInstance().yamlSource.get(key)).toUpperCase());
    }
}
