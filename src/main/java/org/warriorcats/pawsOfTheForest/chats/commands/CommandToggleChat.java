package org.warriorcats.pawsOfTheForest.chats.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;
import org.warriorcats.pawsOfTheForest.core.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.List;

public class CommandToggleChat extends AbstractCommand {

    public static ChatChannel getToggledChat(Player player) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            return entity.getSettings().getToggledChat();
        }
    }

    public static void setToggledChat(Player player, ChatChannel chatToggled) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            session.beginTransaction();
            PlayerEntity senderEntity = session.get(PlayerEntity.class, player.getUniqueId());
            senderEntity.getSettings().setToggledChat(chatToggled);
            session.getTransaction().commit();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.toggle", "/toggle <global|local|clan|roleplay|localroleplay>")) {
            return true;
        }

        ChatChannel chatToggled = ChatChannel.valueOf(args[0].toUpperCase());

        setToggledChat((Player) sender, chatToggled);

        sender.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Chats.CHAT_TOGGLED + " " + chatToggled.toString().toLowerCase());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return List.of("global", "local", "clan", "roleplay", "localroleplay");
            default:
                return null;
        }
    }
}
