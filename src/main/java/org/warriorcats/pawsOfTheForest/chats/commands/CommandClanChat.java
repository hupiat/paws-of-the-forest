package org.warriorcats.pawsOfTheForest.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.List;

public class CommandClanChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 2,
                "warriorcats.chat.clan", "/clan <message>")) {
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                PlayerEntity senderEntity = session.get(PlayerEntity.class, ((Player) sender).getUniqueId());
                PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());

                if (!senderEntity.getClan().getUuid().equals(entity.getClan().getUuid())) {
                    continue;
                }
            }
            player.sendMessage(MessagesConf.Chats.COLOR_CLAN_CHANNEL + "[Clan] " +
                    MessagesConf.Chats.COLOR_SENDER + sender.getName() + ": " +
                    MessagesConf.Chats.COLOR_MESSAGE + args[1]);
        }

        sender.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Chats.MESSAGE_SENT_CLAN);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return List.of("message");
            default:
                return null;
        }
    }
}
