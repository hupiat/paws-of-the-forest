package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.List;

public class CommandRoleplayChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.roleplay", "/roleplay <message>")) {
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
                if (!playerEntity.getSettings().isShowRoleplay()) {
                    continue;
                }
            }

            player.sendMessage(MessagesConf.Chats.COLOR_ROLEPLAY_CHANNEL + "[RP] " +
                    MessagesConf.Chats.getColorName((Player) sender) + sender.getName() + ": " +
                    MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)));
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
