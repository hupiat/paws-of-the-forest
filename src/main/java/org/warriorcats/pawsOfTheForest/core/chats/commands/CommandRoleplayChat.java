package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.messages.MessagesConf;

import java.util.List;

public class CommandRoleplayChat implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("warriorcats.chat.roleplay")) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Chats.NOT_ENOUGH_PERMISSIONS);
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /roleplay <message>");
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(ChatColor.GOLD + "[RolePlay] " +
                    ChatColor.WHITE + sender.getName() + ": " +
                    ChatColor.GRAY + args[1]);
        }

        sender.sendMessage(ChatColor.GREEN + MessagesConf.Chats.MESSAGE_SENT_ROLEPLAY);

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
