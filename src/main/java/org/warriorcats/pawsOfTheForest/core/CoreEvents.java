package org.warriorcats.pawsOfTheForest.core;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;
import org.warriorcats.pawsOfTheForest.chats.commands.CommandToggleChat;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public class CoreEvents implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        // Saving player data at joining if it does not exist
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            session.beginTransaction();

            PlayerEntity existing = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());

            if (existing == null) {
                existing = new PlayerEntity();
                existing.setUuid(event.getPlayer().getUniqueId());
                session.persist(existing);
            }

            session.getTransaction().commit();
        }

        // Toggling default chat
        CommandToggleChat.MAP_CHATS_TOGGLED.put(event.getPlayer().getUniqueId(), ChatChannel.DEFAULT_TOGGLED);
    }

    @EventHandler
    public void on(AsyncChatEvent event) {
        // Handling toggled chats redirections

        event.setCancelled(true);

        ChatChannel chatToggled = CommandToggleChat.MAP_CHATS_TOGGLED.get(event.getPlayer().getUniqueId());
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        Bukkit.dispatchCommand(event.getPlayer(), chatToggled.toString().toLowerCase() + " " + message);
    }
}
