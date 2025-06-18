package org.warriorcats.pawsOfTheForest.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

public abstract class ItemsUtils {

    public static final List<Material> LOOTS_FROM_NPC = List.of(
            Material.EMERALD,
            Material.GOLD_NUGGET,
            Material.IRON_INGOT,
            Material.BREAD,
            Material.APPLE,
            Material.LEATHER,
            Material.STRING
    );

    public static List<Material> getAllFoods() {
        return Arrays.stream(Material.values())
                .filter(Material::isEdible)
                .collect(Collectors.toList());
    }

    public static String serializeItemStackArray(ItemStack[] items) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            dataOutput.writeInt(items.length);

            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            dataOutput.close();
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not serialize item stacks array", e);
        }
        return "";
    }

    public static ItemStack[] deserializeItemStackArray(String data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);

            int length = dataInput.readInt();
            ItemStack[] items = new ItemStack[length];

            for (int i = 0; i < length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (IOException | ClassNotFoundException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not deserialize item stacks array", e);
        }
        return new ItemStack[0];
    }
}
