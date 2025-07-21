package org.warriorcats.pawsOfTheForest.utils;

import com.ticxo.modelengine.api.model.ModeledEntity;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.preys.Prey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class MobsUtils {

    private static final NamespacedKey RABIES_KEY = new NamespacedKey(PawsOfTheForest.getInstance(), "rabies");

    public static List<LivingEntity> getAllEntities() {
        List<LivingEntity> entities = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntities()) {
                if (entity instanceof LivingEntity livingEntity) {
                    entities.add(livingEntity);
                }
            }
        }
        return entities;
    }

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

        Material loot = ItemsUtils.COMMON_LOOTS.get(random.nextInt(ItemsUtils.COMMON_LOOTS.size()));

        int amount = switch (loot) {
            case GOLD_NUGGET, IRON_NUGGET -> 1 + random.nextInt(3);
            default -> 1;
        };

        return new ItemStack(loot, amount);
    }

    public static boolean isRat(Prey prey) {
        return prey.entityType().equalsIgnoreCase("mouse");
    }

    public static ItemStack getRandomLootFromRat() {
        final Random random = new Random();

        Material loot = ItemsUtils.RAT_LOOTS.get(random.nextInt(ItemsUtils.RAT_LOOTS.size()));

        int amount = 1 + random.nextInt(2);

        return new ItemStack(loot, amount);
    }

    public static boolean canBePoisoned(LivingEntity entity) {
        if (entity instanceof Player) return true;

        switch (entity.getType()) {
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case ZOMBIE_HORSE:
            case ZOMBIFIED_PIGLIN:
            case DROWNED:
            case HUSK:
            case STRAY:
            case SKELETON:
            case SKELETON_HORSE:
            case WITHER_SKELETON:
            case WITHER:
            case PHANTOM:
            case VEX:
            case ZOGLIN:
            case WARDEN:
            case PIGLIN_BRUTE:
                return false;

            case SHEEP:
            case COW:
            case PIG:
            case CHICKEN:
            case HORSE:
            case DONKEY:
            case MULE:
            case LLAMA:
            case RABBIT:
            case WOLF:
            case CAT:
            case OCELOT:
            case PARROT:
            case VILLAGER:
            case IRON_GOLEM:
            case SNOW_GOLEM:
            case FOX:
            case PANDA:
            case TURTLE:
            case FROG:
            case AXOLOTL:
                return false;

            default:
                return true;
        }
    }

    public static void markInfectedByRabies(LivingEntity entity) {
        entity.getPersistentDataContainer().set(
                RABIES_KEY,
                PersistentDataType.BYTE,
                (byte) 1
        );
    }

    public static boolean isInfectedWithRabies(LivingEntity entity) {
        return entity.getPersistentDataContainer().has(RABIES_KEY, PersistentDataType.BYTE);
    }

    public static boolean canBeInfectedByRabies(LivingEntity entity) {
        return entity instanceof Wolf || entity instanceof Bat || entity instanceof Cat;
    }

    public static boolean isPredator(LivingEntity entity) {
        return switch (entity.getType()) {
            case WOLF, FOX, OCELOT, CAT -> true;
            default -> false;
        };
    }
}
