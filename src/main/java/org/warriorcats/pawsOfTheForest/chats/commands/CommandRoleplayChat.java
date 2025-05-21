package org.warriorcats.pawsOfTheForest.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.MessagesConf;

import java.util.List;

public class CommandRoleplayChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 2,
                "warriorcats.chat.roleplay", "/roleplay <message>")) {
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
