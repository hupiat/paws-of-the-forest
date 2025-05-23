package org.warriorcats.pawsOfTheForest.core.settings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public class SettingsEvents implements Listener {

    // Handling chat settings
    @EventHandler
    public void on(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals("⚙️ Chat Settings")) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();

        if (slot == 10) {
            Bukkit.getScheduler().runTaskAsynchronously(PawsOfTheForest.getInstance(), () -> {
                try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                    boolean current = entity.getSettings().isShowRoleplay();
                    entity.getSettings().setShowRoleplay(!current);
                    if (!entity.getSettings().isShowRoleplay() &&
                            (entity.getSettings().getToggledChat() == ChatChannel.ROLEPLAY || entity.getSettings().getToggledChat() == ChatChannel.LOCALROLEPLAY)) {
                        entity.getSettings().setToggledChat(ChatChannel.DEFAULT_TOGGLED);
                    }
                    session.getTransaction().commit();
                }
                Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
                    player.openInventory(SettingsMenu.create(player));
                });
            });
        }

        if (slot == 12) {
            Bukkit.getScheduler().runTaskAsynchronously(PawsOfTheForest.getInstance(), () -> {
                try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                    ChatChannel current = entity.getSettings().getToggledChat();
                    ChatChannel next = SettingsMenu.getNextChat(player, current);
                    entity.getSettings().setToggledChat(next);
                    session.getTransaction().commit();
                }
                Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
                    player.openInventory(SettingsMenu.create(player));
                });
            });
        }
    }

    // Handling Noteblock item interaction
    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.NOTE_BLOCK) return;
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            player.openInventory(SettingsMenu.create(player));
            event.setCancelled(true);
        }
    }
}
