package org.warriorcats.pawsOfTheForest.skills.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.commands.AbstractCommand;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.skills.menus.MenuBackpack;

import java.util.List;

public class CommandOpenBackpack extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 0,
                "warriorcats.backpack", "/backpack")) {
            return true;
        }

        PlayerEntity entity = EventsCore.PLAYERS_CACHE.get(((Player) sender).getUniqueId());
        if (!entity.hasAbility(Skills.BEAST_OF_BURDEN)) {
            sender.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_BEAST_OF_BURDEN_NOT_UNLOCKED);
            return true;
        }

        int tier = entity.getAbilityTier(Skills.BEAST_OF_BURDEN);

        MenuBackpack.open((Player) sender, tier);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
