package org.warriorcats.pawsOfTheForest.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.MessagesConf;

import java.util.List;

public class CommandGlobalChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 2,
                "warriorcats.chat.global", "/global <message>")) {
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(MessagesConf.Chats.COLOR_STANDARD_CHANNEL + "[Global] " +
                    MessagesConf.Chats.COLOR_SENDER + sender.getName() + ": " +
                    MessagesConf.Chats.COLOR_MESSAGE + args[1]);
        }

        sender.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Chats.MESSAGE_SENT_GLOBAL);

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
