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

public class CommandPrivateMessageReplyChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.reply", "/reply <message>")) {
            return true;
        }

        Player player = null;

        // First, replying to direct messages, by looking for the most recent Date
        UUID uuid = CommandPrivateMessageChat.PRIVATE_MESSAGES_MAP.values().stream()
                .filter(pair -> pair.getKey().equals(((Player) sender).getUniqueId()))
                .min((pair1, pair2) -> pair2.getValue().compareTo(pair1.getValue()))
                .map(Pair::getKey)
                .orElse(null);

        if (uuid != null) {
            player = Bukkit.getPlayer(uuid);
        }

        if (player == null) {
            // If no one, replying to last sent player
            player = Bukkit.getPlayer(CommandPrivateMessageChat.PRIVATE_MESSAGES_MAP.get(((Player) sender).getUniqueId()).getKey());
        }

        if (player == null) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Chats.PLAYER_NOT_FOUND);
            return true;
        } else {
            // Then, updating the Map just as we were running /message command
            CommandPrivateMessageChat.PRIVATE_MESSAGES_MAP.put(((Player) sender).getUniqueId(), Pair.of(player.getUniqueId(), new Date()));
        }

        String message = String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length));

        player.sendMessage(MessagesConf.Chats.COLOR_PRIVATE_MESSAGE + "[" + sender.getName() + " -> " + player.getName() + "] " +
                MessagesConf.Chats.COLOR_MESSAGE + message);

        sender.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Chats.MESSAGE_SENT + " " + player.getName() + ".");

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
