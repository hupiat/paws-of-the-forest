package org.warriorcats.pawsOfTheForest.core.chats.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;

import java.util.List;

public class CommandLocalRoleplayChat extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 1,
                "warriorcats.chat.localroleplay", "/localroleplay <message>")) {
            return true;
        }

        Location senderLocation = ((Player) sender).getLocation();
        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerEntity playerEntity = EventsCore.PLAYERS_CACHE.get(player.getUniqueId());
            if (!playerEntity.getSettings().isShowRoleplay()) {
                continue;
            }

            boolean x = Math.abs(player.getLocation().getX() - senderLocation.getX()) < ChatChannels.LOCAL_CHANNEL_RADIUS;
            boolean y = Math.abs(player.getLocation().getY() - senderLocation.getY()) < ChatChannels.LOCAL_CHANNEL_RADIUS;
            boolean z = Math.abs(player.getLocation().getZ() - senderLocation.getZ()) < ChatChannels.LOCAL_CHANNEL_RADIUS;

            if (x && y && z) {
                player.sendMessage(formatWithClanPrefixIfPresent(MessagesConf.Chats.COLOR_ROLEPLAY_CHANNEL + "[Local RP]",
                        MessagesConf.Chats.COLOR_PLAYER_NAME_DEFAULT + sender.getName() + ": " +
                        MessagesConf.Chats.COLOR_MESSAGE + String.join(" ", java.util.Arrays.copyOfRange(args, 0, args.length)), (Player) sender));
            }
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
