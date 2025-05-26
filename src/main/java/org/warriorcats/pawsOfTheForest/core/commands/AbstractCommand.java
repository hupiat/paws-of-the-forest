package org.warriorcats.pawsOfTheForest.core.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    protected boolean checkForPermissionsAndArgs(CommandSender sender, String[] args, int argsLength, String permission, String usage) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_ENOUGH_PERMISSIONS);
            return false;
        }

        if (args.length < argsLength) {
            sender.sendMessage(ChatColor.RED + "Usage: " + usage);
            return false;
        }

        return true;
    }
}
