package org.warriorcats.pawsOfTheForest.core.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.shops.MenuShop;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.List;

public class CommandOpenShop extends AbstractCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!checkForPermissionsAndArgs(sender, args, 0,
                "warriorcats.shop", "/shop")) {
            return true;
        }

        MenuShop.open((Player) sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }
}
