package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;

import java.util.List;

public class CommandGlobalChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.global", "/global <message>")) {
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(formatWithClanPrefixIfPresent(MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + sender.getName(),
                     ": " + MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)), (Player) sender));
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
