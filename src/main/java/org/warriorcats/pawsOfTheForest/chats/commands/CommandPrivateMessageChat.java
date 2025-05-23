package org.warriorcats.pawsOfTheForest.chats.commands;

import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.MessagesConf;

import java.util.*;

public class CommandPrivateMessageChat extends AbstractCommand {

    public static final Map<UUID, Pair<UUID, Date>> PRIVATE_MESSAGES_MAP = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 2,
                "warriorcats.chat.message", "/message <player> <message>")) {
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Chats.PLAYER_NOT_FOUND);
        }

        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));

        player.sendMessage(MessagesConf.Chats.COLOR_PRIVATE_MESSAGE + "[" + sender.getName() + " -> " + player.getName() + "] " +
                MessagesConf.Chats.COLOR_MESSAGE + message);

        PRIVATE_MESSAGES_MAP.put(((Player) sender).getUniqueId(), Pair.of(player.getUniqueId(), new Date()));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return List.of("player");
            case 2:
                return List.of("message");
            default:
                return null;
        }
    }
}
