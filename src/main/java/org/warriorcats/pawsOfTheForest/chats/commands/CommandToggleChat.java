package org.warriorcats.pawsOfTheForest.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;
import org.warriorcats.pawsOfTheForest.core.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CommandToggleChat extends AbstractCommand {

    public static final Map<UUID, ChatChannel> MAP_CHATS_TOGGLED = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 2,
                "warriorcats.chat.toggle", "/toggle <global|local|clan|roleplay|localroleplay>")) {
            return true;
        }

        ChatChannel chatToggled = ChatChannel.valueOf(args[1].toUpperCase());

        MAP_CHATS_TOGGLED.put(((Player) sender).getUniqueId(), chatToggled);

        sender.sendMessage(MessagesConf.Chats.COLOR_FEEDBACK + MessagesConf.Chats.CHAT_TOGGLED + " " + chatToggled.toString().toLowerCase());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return List.of("global", "local", "clan", "roleplay", "localroleplay");
            default:
                return null;
        }
    }
}
