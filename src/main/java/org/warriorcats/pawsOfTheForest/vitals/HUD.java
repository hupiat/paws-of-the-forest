package org.warriorcats.pawsOfTheForest.vitals;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class HUD {

    public static final char THIRST_FULL_ICON = '\uE000';
    public static final char THIRST_EMPTY_ICON = '\uE001';
    public static final char ENERGY_FULL_ICON = '\uE002';
    public static final char ENERGY_EMPTY_ICON = '\uE003';
    public static final char HYGIENE_FULL_ICON = '\uE004';
    public static final char HYGIENE_EMPTY_ICON = '\uE005';
    public static final char SOCIAL_ICON = '\uE006';

    private static final Map<UUID, NamespacedKey> PROGRESS_BARS = new ConcurrentHashMap<>();
    private static final Map<UUID, NamespacedKey> DUMMY_BARS = new ConcurrentHashMap<>();

    private static final Map<UUID, BukkitTask> SOCIAL_ACTION_BAR_TASKS = new ConcurrentHashMap<>();

    public static void open(Player player) {
        for (@NotNull Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
            BossBar b = it.next();
            b.removePlayer(player);
        }
        updateInterface(player);
    }

    public static void updateInterface(Player player) {
        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());

        remove(player);

        String barText = createPartialBar(entity.getThirst(), THIRST_FULL_ICON, THIRST_EMPTY_ICON) + "  " +
                createPartialBar(entity.getEnergy(), ENERGY_FULL_ICON, ENERGY_EMPTY_ICON) + "  " +
                createPartialBar(entity.getHygiene(), HYGIENE_FULL_ICON, HYGIENE_EMPTY_ICON);

        UUID uuid = player.getUniqueId();

        // Dummy bar to scale vertically
        NamespacedKey dummyKey = NamespacedKey.minecraft("hud_dummy_" + uuid);
        BossBar dummy = Bukkit.createBossBar(dummyKey, " ", BarColor.WHITE, BarStyle.SEGMENTED_20);
        dummy.addPlayer(player);
        DUMMY_BARS.put(uuid, dummyKey);

        // HUD bar
        NamespacedKey key = NamespacedKey.minecraft("hud_" + uuid);
        BossBar bar = Bukkit.createBossBar(key, barText, BarColor.WHITE, BarStyle.SEGMENTED_20);
        bar.addPlayer(player);
        PROGRESS_BARS.put(uuid, key);

        // Action bar (social)
        String socialText = SOCIAL_ICON + "  " + (int) (entity.getSocial() * 100) + "%";

        BukkitTask previous = SOCIAL_ACTION_BAR_TASKS.remove(uuid);
        if (previous != null && !previous.isCancelled()) {
            previous.cancel();
        }
        SOCIAL_ACTION_BAR_TASKS.put(uuid, new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }
                player.sendActionBar(Component.text(socialText));
            }
        }.runTaskTimer(PawsOfTheForest.getInstance(), 0L, 20L));
    }

    public static void remove(Player player) {
        UUID uuid = player.getUniqueId();

        NamespacedKey hudKey = PROGRESS_BARS.remove(uuid);
        if (hudKey != null) {
            BossBar bar = Bukkit.getBossBar(hudKey);
            if (bar != null) {
                bar.removeAll();
                Bukkit.removeBossBar(hudKey);
            }
        }

        NamespacedKey dummyKey = DUMMY_BARS.remove(uuid);
        if (dummyKey != null) {
            BossBar dummy = Bukkit.getBossBar(dummyKey);
            if (dummy != null) {
                dummy.removeAll();
                Bukkit.removeBossBar(dummyKey);
            }
        }
    }

    private static String createPartialBar(double value, char fullChar, char emptyChar) {
        int total = 10;
        value = Math.max(0.0, Math.min(1.0, value));
        int filled = (int) Math.round(value * total);
        int empty = total - filled;
        return String.valueOf(fullChar).repeat(filled) + String.valueOf(emptyChar).repeat(empty);
    }
}