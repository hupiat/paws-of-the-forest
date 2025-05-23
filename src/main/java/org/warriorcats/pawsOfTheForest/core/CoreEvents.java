package org.warriorcats.pawsOfTheForest.core;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;
import org.warriorcats.pawsOfTheForest.chats.commands.CommandToggleChat;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsMenu;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public class CoreEvents implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        // Saving player data at joining if it does not exist
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {

            PlayerEntity existing = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());

            if (existing == null) {
                session.beginTransaction();
                existing = new PlayerEntity();
                existing.setUuid(event.getPlayer().getUniqueId());
                existing.setSettings(new SettingsEntity());
                session.persist(existing);
                session.getTransaction().commit();
            }

        }

        // Toggling default chat
        CommandToggleChat.setToggledChat(event.getPlayer(), ChatChannel.DEFAULT_TOGGLED);
    }

    // Handling toggled chats redirections
    @EventHandler
    public void on(AsyncChatEvent event) {

        event.setCancelled(true);

        ChatChannel chatToggled = CommandToggleChat.getToggledChat(event.getPlayer());
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
            Bukkit.dispatchCommand(event.getPlayer(), chatToggled.toString().toLowerCase() + " " + message);
        });
    }

    // Handling chat settings
    @EventHandler
    public void on(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals("⚙️ Chat Settings")) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();

        if (slot == 12) {
            Bukkit.getScheduler().runTaskAsynchronously(PawsOfTheForest.getInstance(), () -> {
                try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                    session.beginTransaction();
                    PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                    ChatChannel current = entity.getSettings().getToggledChat();
                    ChatChannel next = SettingsMenu.getNextChat(current);
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
