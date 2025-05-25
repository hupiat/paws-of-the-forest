package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.Bukkit;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;

import java.io.*;
import java.util.Properties;
import java.util.logging.Level;

public abstract class FileUtils {

    private static final File PLUGIN_DATA_FOLDER = PawsOfTheForest.getInstance().getDataFolder();

    public static Properties load(String fileName, Properties source) {
        File file = new File(PLUGIN_DATA_FOLDER, fileName);

        // If the file does not exist, copying it from the jar
        if (!file.exists()) {
            try (InputStream in = PawsOfTheForest.class.getClassLoader().getResourceAsStream(fileName)) {
                if (in != null) {
                    file.getParentFile().mkdirs();
                    try (OutputStream out = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = in.read(buffer)) != -1) {
                            out.write(buffer, 0, len);
                        }
                    }
                }
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not copy default config: " + fileName, e);
            }
        }

        try (InputStream input = new FileInputStream(file)) {
            source.load(input);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load " + fileName, ex);
        }

        return source;
    }

    public static void store(String fileName, Properties config) {
        File file = new File(PLUGIN_DATA_FOLDER, fileName);

        try (OutputStream output = new FileOutputStream(file)) {
            config.store(output, "Updated by PawsOfTheForest plugin");
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not store in properties file: " + fileName, e);
        }
    }
}
