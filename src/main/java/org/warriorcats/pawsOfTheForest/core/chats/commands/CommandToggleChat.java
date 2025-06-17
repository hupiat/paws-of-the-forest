package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.Arrays;
import java.util.List;

public class CommandToggleChat extends AbstractCommand {

    public static ChatChannels getToggledChat(Player player) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            return entity.getSettings().getToggledChat();
        }
    }

    public static void setToggledChat(Player player, ChatChannels chatToggled) {
        HibernateUtils.withTransaction(((transaction, session) -> {
            PlayerEntity senderEntity = session.get(PlayerEntity.class, player.getUniqueId());
            senderEntity.getSettings().setToggledChat(chatToggled);
            return senderEntity;
        }));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.toggle", "/toggle <global|local|clan|roleplay|localroleplay>")) {
            return true;
        }

        ChatChannels chatToggled = ChatChannels.valueOf(args[0].toUpperCase());

        if (chatToggled == ChatChannels.CLAN) {
            try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                PlayerEntity senderEntity = session.get(PlayerEntity.class, ((Player) sender).getUniqueId());
                if (senderEntity.getClan() == null) {
                    sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_A_CLAN_MEMBER);
                    return true;
                }
            }
        }

        if (ChatChannels.isRoleplay(chatToggled)) {
            try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                PlayerEntity senderEntity = session.get(PlayerEntity.class, ((Player) sender).getUniqueId());
                if (!senderEntity.getSettings().isShowRoleplay()) {
                    sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_SHOWING_ROLEPLAY);
                    return true;
                }
            }
        }

        setToggledChat((Player) sender, chatToggled);

        sender.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Chats.CHAT_TOGGLED + " " + chatToggled.name().toLowerCase());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return List.of(Arrays.stream(ChatChannels.values()).map(e -> e.name().toLowerCase()).toArray(String[]::new));
            default:
                return null;
        }
    }
}
