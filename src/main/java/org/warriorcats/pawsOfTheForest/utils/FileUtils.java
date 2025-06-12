package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public abstract class FileUtils {

    public static final File PLUGIN_DATA_FOLDER = PawsOfTheForest.getInstance().getDataFolder();

    public static final String RESOURCES_PACK_PATH = "resources_pack.zip";

    public static boolean isYaml(String fileName) {
        return fileName.endsWith(".yaml");
    }

    public static <T> T load(String fileName, T source) {
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

        if (isYaml(fileName)) {
            source = (T) YamlConfiguration.loadConfiguration(file);
        } else {
            // Properties file
            try (InputStream input = new FileInputStream(file)) {
                ((Properties) source).load(input);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not load: " + fileName, ex);
            }
        }

        return source;
    }

    public static <T> void store(String fileName, T config) {
        File file = new File(PLUGIN_DATA_FOLDER, fileName);

        try (OutputStream output = new FileOutputStream(file)) {
            if (isYaml(fileName)) {
                ((YamlConfiguration) config).save(file);
            } else {
                // Properties file
                ((Properties) config).store(output, "Updated by PawsOfTheForest plugin");
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not store in config file: " + fileName, e);
        }
    }

    public static void copyFolder(Path source, Path target) {
        try {
            Files.walkFileTree(source, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path rel = source.relativize(dir);
                    Path destDir = target.resolve(rel);
                    if (Files.notExists(destDir)) {
                        Files.createDirectories(destDir);
                    }
                    return FileVisitResult.CONTINUE;
                }
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path rel = source.relativize(file);
                    Path destFile = target.resolve(rel);
                    Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to copy directory", e);
        }
    }

    public static void deleteFolder(Path path) {
        try {
            if (Files.exists(path)) {
                Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                Bukkit.getLogger().log(Level.WARNING, "Failed to delete: " + p, e);
                            }
                        });
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to delete directory", e);
        }
    }

    public static void unzipFolder(Path zipFile, Path targetDir) {
        try (ZipFile zip = new ZipFile(zipFile.toFile())) {
            if (Files.notExists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path outPath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    try (InputStream is = zip.getInputStream(entry)) {
                        Files.copy(is, outPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while unzipping", e);
        }
    }

    public static void zipFolder(Path sourceFolderPath, Path zipPath) {
        Path tempCopy = PLUGIN_DATA_FOLDER.toPath().resolve("resources_pack");

        try {
            // Copy the source folder to a temp location
            Files.walk(sourceFolderPath).forEach(source -> {
                try {
                    Path destination = tempCopy.resolve(sourceFolderPath.relativize(source));
                    if (Files.isDirectory(source)) {
                        Files.createDirectories(destination);
                    } else {
                        Files.copy(source, destination);
                    }
                } catch (IOException e) {
                    Bukkit.getLogger().log(Level.SEVERE, "Error copying resources for zipping", e);
                }
            });

            // Zip the copied folder into the destination .zip
            try (ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                Files.walk(tempCopy)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            ZipEntry zipEntry = new ZipEntry(tempCopy.relativize(path).toString().replace("\\", "/"));
                            try {
                                zs.putNextEntry(zipEntry);
                                Files.copy(path, zs);
                                zs.closeEntry();
                            } catch (IOException e) {
                                Bukkit.getLogger().log(Level.SEVERE, "Error zipping resources", e);
                            }
                        });
            }

        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Failed to create zipped file", e);
        } finally {
            deleteFolder(tempCopy);
        }
    }

}
