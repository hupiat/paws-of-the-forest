package org.warriorcats.pawsOfTheForest.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

public class CommandLocalRoleplayChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.localroleplay", "/localroleplay <message>")) {
            return true;
        }

        Location senderLocation = ((Player) sender).getLocation();
        for (Player player : Bukkit.getOnlinePlayers()) {
            try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
                if (!playerEntity.getSettings().isShowRoleplay()) {
                    continue;
                }
            }

            boolean x = Math.abs(player.getLocation().getX() - senderLocation.getX()) < ChatChannel.LOCAL_CHANNEL_RADIUS;
            boolean y = Math.abs(player.getLocation().getY() - senderLocation.getY()) < ChatChannel.LOCAL_CHANNEL_RADIUS;
            boolean z = Math.abs(player.getLocation().getZ() - senderLocation.getZ()) < ChatChannel.LOCAL_CHANNEL_RADIUS;

            if (x && y && z) {
                player.sendMessage(MessagesConf.Chats.COLOR_ROLEPLAY_CHANNEL + "[Local RolePlay] " +
                        MessagesConf.Chats.COLOR_SENDER + sender.getName() + ": " +
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
