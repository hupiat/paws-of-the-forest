package org.warriorcats.pawsOfTheForest.preys;

import com.comphenix.protocol.wrappers.EnumWrappers;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import lombok.Getter;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.warriorcats.pawsOfTheForest.utils.PacketsUtils;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
public class PreyMouse implements Prey {

    public enum Variant {
        HOUSE, WOOD, WHITE_FOOTED
    }

    private final Location location;
    private final Variant variant;
    private final int entityId = PacketsUtils.nextEntityId();
    private final Set<UUID> visibleTo = new HashSet<>();

    public static final String VARIANT_HOUSE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTcxNGMyZTU4NjE2ZWVlOTgzMmZlMGRkOGM4ZTY1MGE4YzBjOTAwZTM5ZWIyMTg3Njg0ODU2YmM5MDllZDZhMSJ9fX0=";
    public static final String VARIANT_WOOD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTcxNjBjNjI4ODc2YTUyODVjYjliNzk2YzE4M2RlNjNjMjBjOTc0MDU4NDU4Y2MyMDUxNWMzZGZlZmYyMjg4MCJ9fX0=";
    public static final String VARIANT_WHITE_FOOTED = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTcxNGMyZTU4NjE2ZWVlOTgzMmZlMGRkOGM4ZTY1MGE4YzBjOTAwZTM5ZWIyMTg3Njg0ODU2YmM5MDllZDZhMSJ9fX0=";

    public PreyMouse(Location location, Variant variant) {
        this.location = location.clone();
        this.variant = variant;
    }

    @Override
    public void spawn() {
        ItemStack head = getMouseHead();

        for (Player player : Bukkit.getOnlinePlayers()) {
            PacketsUtils.createEntity(player, entityId, EntityType.ARMOR_STAND, location);
            PacketsUtils.setEquipment(player, entityId, EnumWrappers.ItemSlot.HEAD, head);

            net.minecraft.world.level.Level nmsWorld = ((CraftPlayer) player).getHandle().level();

            ArmorStand dummy = PacketsUtils.createDummyEntity(entityId, nmsWorld, location);

            net.minecraft.network.syncher.SynchedEntityData watcher = dummy.getEntityData();

            List<net.minecraft.network.syncher.SynchedEntityData.DataValue<?>> metadata = watcher.getNonDefaultValues();

            PacketsUtils.setMetadata(player, entityId, metadata);

            visibleTo.add(player.getUniqueId());

            ia(dummy, player);
        }
    }

    @Override
    public void remove() {
        for (UUID uuid : visibleTo) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                PacketsUtils.destroyEntity(player, entityId);
            }
        }
        visibleTo.clear();
    }

    private ItemStack getMouseHead() {
        String texture = switch (variant) {
            case HOUSE -> VARIANT_HOUSE;
            case WOOD -> VARIANT_WOOD;
            case WHITE_FOOTED -> VARIANT_WHITE_FOOTED;
        };

        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        if (meta != null) {
            PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), "MouseSkin");
            profile.setProperty(new ProfileProperty("textures", texture));
            meta.setPlayerProfile(profile);
            head.setItemMeta(meta);
        }

        return head;
    }
}
