package org.warriorcats.pawsOfTheForest.core.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

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

    protected String formatWithClanPrefixIfPresent(String prefix, String message, Player sender) {
        PlayerEntity playerEntity = EventsCore.PLAYER_CACHE.get(sender.getUniqueId());
        if (playerEntity.getClan() != null) {
            return playerEntity.getClan().getColorCode() + "[" + playerEntity.getClan().toString() + "]" + " " + prefix + " " + message;
        }
        return prefix + " " + message;
    }
}
