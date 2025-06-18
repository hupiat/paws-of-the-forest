package org.warriorcats.pawsOfTheForest.utils;

import com.ticxo.modelengine.api.model.ModeledEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

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

    public static ItemStack getRandomDropFood(int minAmount, int maxAmount) {
        final Random random = new Random();
        List<Material> foods = ItemsUtils.getAllFoods();
        Material food = foods.get(random.nextInt(foods.size()));
        int qty = minAmount + random.nextInt(maxAmount - minAmount + 1);
        return new ItemStack(food, qty);
    }

    public static boolean isStealthFrom(Player player, LivingEntity entity) {
        return player.isSneaking() || player.isInvisible() || !entity.hasLineOfSight(player);
    }

    public static ItemStack getRandomLootFromStealing() {
        final Random random = new Random();

        Material loot = ItemsUtils.LOOTS_FROM_NPC.get(random.nextInt(ItemsUtils.LOOTS_FROM_NPC.size()));

        int amount = switch (loot) {
            case EMERALD, IRON_INGOT -> 1;
            case GOLD_NUGGET -> 1 + random.nextInt(3);
            default -> 1;
        };

        return new ItemStack(loot, amount);
    }
}
