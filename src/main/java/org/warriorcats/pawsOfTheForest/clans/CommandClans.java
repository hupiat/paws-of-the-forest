package org.warriorcats.pawsOfTheForest.clans;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.Arrays;
import java.util.List;

public class CommandClans extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        final String usage = "/clans <clan> <add|remove> <player>";
        if (!checkForPermissionsAndArgs(sender, args, 3,
                "warriorcats.clans", usage)) {
            return true;
        }

        Clans clan;
        try {
            clan = Clans.from(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Clans.CLAN_NOT_FOUND);
            return true;
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + MessagesConf.Chats.PLAYER_NOT_FOUND);
                return true;
            }
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            var transaction = session.beginTransaction();
            switch (args[1].toLowerCase()) {
                case "add":
                    playerEntity.setClan(clan);
                    player.sendMessage(MessagesConf.Clans.COLOR_FEEDBACK + MessagesConf.Clans.CLAN_ADDED + " " + clan);
                    break;
                case "remove":
                    if (playerEntity.getClan() != clan) {
                        sender.sendMessage(ChatColor.RED + MessagesConf.Clans.PLAYER_NOT_BELONG_TO_CLAN);
                        return true;
                    }
                    playerEntity.setClan(null);
                    player.sendMessage(MessagesConf.Clans.COLOR_FEEDBACK + MessagesConf.Clans.CLAN_REMOVED + " " + clan);
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + MessagesConf.GENERIC_ERROR + " " + usage);
                    return true;
            }
            EventsCore.PLAYER_CACHE.put(player.getUniqueId(), playerEntity);
            transaction.commit();
        }


        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1:
                return List.of(Arrays.stream(Clans.values()).map(Clans::toString).toArray(String[]::new));
            case 2:
                return List.of("add", "remove");
            case 3:
                return List.of("player");
            default:
                return null;
        }
    }
}
