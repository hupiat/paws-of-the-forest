package org.warriorcats.pawsOfTheForest.core;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.chats.commands.CommandToggleChat;
import org.warriorcats.pawsOfTheForest.core.huds.HUD;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranchEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.HttpServerUtils;

public class EventsCore implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        // Saving player data at joining if it does not exist
        HibernateUtils.withSession(session -> {
            PlayerEntity existing = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());

            if (existing == null) {
                session.beginTransaction();
                existing = new PlayerEntity();
                existing.setUuid(event.getPlayer().getUniqueId());
                existing.setName(event.getPlayer().getName());
                existing.setSettings(new SettingsEntity());
                for (SkillBranches branche : SkillBranches.values()) {
                    SkillBranchEntity brancheEntity = new SkillBranchEntity();
                    brancheEntity.setBranch(branche);
                    existing.getSkillBranches().add(brancheEntity);
                }
                session.persist(existing);
                session.getTransaction().commit();
            }
        });

        // Toggling HUD
        HUD.open(event.getPlayer());

        // Toggling resources pack
        event.getPlayer().setResourcePack("http://localhost:" + HttpServerUtils.RESOURCES_PACK_PORT + "/" + FileUtils.RESOURCES_PACK_PATH);

        // Toggling default chat
        CommandToggleChat.setToggledChat(event.getPlayer(), ChatChannels.DEFAULT_TOGGLED);
    }

    // Handling toggled chats redirections
    @EventHandler
    public void on(AsyncChatEvent event) {

        event.setCancelled(true);

        ChatChannels chatToggled = CommandToggleChat.getToggledChat(event.getPlayer());
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
            Bukkit.dispatchCommand(event.getPlayer(), chatToggled.name().toLowerCase() + " " + message);
        });
    }
}
