package org.warriorcats.pawsOfTheForest.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;

import java.util.List;

public class CommandList extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 0,
                "warriorcats.list", "/list")) {
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            sender.sendMessage(MessagesConf.Chats.getColorName(player) + player.getName());
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
