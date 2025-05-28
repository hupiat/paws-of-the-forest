package org.warriorcats.pawsOfTheForest.utils;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import static com.comphenix.protocol.PacketType.Play.Server.*;

public abstract class PacketsUtils {
    private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
    private static final Random random = new Random();

    public static int nextEntityId() {
        return random.nextInt(1_000_000) + 20_000;
    }

    public static void createEntity(Player player, int id, EntityType type, Location loc) {
        PacketContainer packet = new PacketContainer(SPAWN_ENTITY);
        packet.getIntegers().write(0, id);
        packet.getUUIDs().write(0, UUID.randomUUID());
        packet.getEntityTypeModifier().write(0, type);
        packet.getDoubles()
                .write(0, loc.getX())
                .write(1, loc.getY())
                .write(2, loc.getZ());
        sendPacket(player, packet);
    }

    public static ArmorStand createDummyEntity(int entityId, net.minecraft.world.level.Level nmsWorld, Location location) {
        ArmorStand dummy = new ArmorStand(nmsWorld, 0, 0, 0);
        dummy.setId(entityId);
        dummy.setInvisible(true);
        dummy.setMarker(true);
        dummy.setNoGravity(false);
        dummy.setCustomNameVisible(false);
        dummy.setSmall(true);
        dummy.setShowArms(false);
        dummy.setNoBasePlate(true);
        dummy.setPos(location.getX(), location.getY(), location.getZ());
        return dummy;
    }

    public static void destroyEntity(Player player, int id) {
        PacketContainer packet = new PacketContainer(ENTITY_DESTROY);
        packet.getIntegerArrays().write(0, new int[]{id});
        sendPacket(player, packet);
    }

    public static void setMetadata(Player bukkitPlayer, int entityId, List<SynchedEntityData.DataValue<?>> metadata) {
        ServerPlayer player = ((CraftPlayer) bukkitPlayer).getHandle();
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(entityId, metadata);
        player.connection.send(packet);
    }

    public static void setEquipment(Player player, int id, EnumWrappers.ItemSlot slot, ItemStack item) {
        PacketContainer packet = new PacketContainer(ENTITY_EQUIPMENT);
        packet.getIntegers().write(0, id);
        packet.getSlotStackPairLists().write(0, Collections.singletonList(
                new Pair<>(slot, item)
        ));
        sendPacket(player, packet);
    }

    public static void moveEntity(Entity entity, Player player) {
        var packet = new net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket(entity);
        ((org.bukkit.craftbukkit.entity.CraftPlayer) player).getHandle().connection.send(packet);
    }

    private static void sendPacket(Player player, PacketContainer packet) {
        try {
            manager.sendServerPacket(player, packet);
        } catch (Exception ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while sending packet", ex);
        }
    }
}
