package org.warriorcats.pawsOfTheForest.core;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;
import org.warriorcats.pawsOfTheForest.chats.commands.CommandToggleChat;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
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
}
