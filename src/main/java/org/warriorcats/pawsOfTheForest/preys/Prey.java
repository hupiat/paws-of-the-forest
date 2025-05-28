package org.warriorcats.pawsOfTheForest.preys;

import net.minecraft.world.entity.Entity;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.utils.PacketsUtils;

public interface Prey {

    ChatColor COLOR_NAME = ChatColor.GRAY;

    void spawn();

    void remove();

    default void ia(Entity entity, Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {

                // Movements
                double dx = entity.getX() + (Math.random() - 0.5) * 0.1;
                double dz = entity.getZ() + (Math.random() - 0.5) * 0.1;

                entity.setPos(dx, entity.getY(), dz);

                // Orientation
                float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
                entity.setYRot(yaw);

                PacketsUtils.moveEntity(entity, player);
            }
        }.runTaskTimer(PawsOfTheForest.getInstance(), 0L, 10L);
    }
}
