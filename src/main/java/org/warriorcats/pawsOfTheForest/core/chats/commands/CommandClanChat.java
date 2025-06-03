package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.List;

public class CommandClanChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.clan", "/clan <message>")) {
            return true;
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            PlayerEntity senderEntity = session.get(PlayerEntity.class, ((Player) sender).getUniqueId());

            if (senderEntity.getClan() == null) {
                sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_A_CLAN_MEMBER);
                return true;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                if (senderEntity.getClan() != entity.getClan()) {
                    continue;
                }
                player.sendMessage(MessagesConf.Chats.COLOR_CLAN_CHANNEL + "[Clan] " +
                        MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + sender.getName() + ": " +
                        MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)));
            }
        }

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
