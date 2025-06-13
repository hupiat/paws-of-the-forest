package org.warriorcats.pawsOfTheForest.core.huds;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public abstract class HUD {

    private static final Map<UUID, NamespacedKey> PROGRESS_BARS = new HashMap<>();

    public static void open(Player player) {
        for (@NotNull Iterator<KeyedBossBar> it = Bukkit.getBossBars(); it.hasNext(); ) {
            BossBar b = it.next();
            b.removePlayer(player);
        }
        updateInterface(player);
    }

    public static void updateInterface(Player player) {
        NamespacedKey oldKey = PROGRESS_BARS.remove(player.getUniqueId());
        if (oldKey != null) {
            BossBar oldBar = Bukkit.getBossBar(oldKey);
            oldBar.removeAll();
            Bukkit.removeBossBar(oldKey);
        }
        NamespacedKey key = NamespacedKey.minecraft(player.getUniqueId().toString().toLowerCase());
        BossBar bar = Bukkit.createBossBar(key,
                player.getName() + " - " + player.getLevel(), BarColor.BLUE, BarStyle.SEGMENTED_10);
        PROGRESS_BARS.put(player.getUniqueId(), key);
        bar.addPlayer(player);

        bar.setProgress(player.getExp());
    }
}
