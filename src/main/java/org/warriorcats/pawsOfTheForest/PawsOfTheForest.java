package org.warriorcats.pawsOfTheForest;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.warriorcats.pawsOfTheForest.core.EventsCore;
import org.warriorcats.pawsOfTheForest.core.chats.commands.*;
import org.warriorcats.pawsOfTheForest.core.commands.CommandCoins;
import org.warriorcats.pawsOfTheForest.core.commands.CommandList;
import org.warriorcats.pawsOfTheForest.core.commands.CommandOpenShop;
import org.warriorcats.pawsOfTheForest.core.commands.CommandXp;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.core.settings.EventsSettings;
import org.warriorcats.pawsOfTheForest.shops.EventsShop;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public final class PawsOfTheForest extends JavaPlugin {

    private static PawsOfTheForest INSTANCE;

    public PawsOfTheForest() {
        INSTANCE = this;
    }

    public static PawsOfTheForest getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        // Initializing database
        HibernateUtils.getSessionFactory();
        getLogger().info("Hibernate/MySQL connected.");

        // Loading config
        MessagesConf.load(MessagesConf.CONFIG_FILE_NAME);
        ShopsConf.load(ShopsConf.CONFIG_FILE_NAME);

        // Registering commands
        registerCommand("global", new CommandGlobalChat());
        registerCommand("local", new CommandLocalChat());
        registerCommand("clan", new CommandClanChat());
        registerCommand("roleplay", new CommandRoleplayChat());
        registerCommand("localroleplay", new CommandLocalRoleplayChat());
        registerCommand("message", new CommandPrivateMessageChat());
        registerCommand("reply", new CommandPrivateMessageReplyChat());
        registerCommand("toggle", new CommandToggleChat());
        registerCommand("list", new CommandList());
        registerCommand("coins", new CommandCoins());
        registerCommand("xp", new CommandXp());
        registerCommand("shop", new CommandOpenShop());

        // Registering events
        this.getServer().getPluginManager().registerEvents(new EventsCore(), INSTANCE);
        this.getServer().getPluginManager().registerEvents(new EventsSettings(), INSTANCE);
        this.getServer().getPluginManager().registerEvents(new EventsShop(), INSTANCE);
    }

    @Override
    public void onDisable() {
        HibernateUtils.shutdown();
    }

    private void registerCommand(String name, Object instance) {
        if (instance instanceof CommandExecutor executor) {
            INSTANCE.getCommand(name).setExecutor(executor);
        }
        if (instance instanceof TabCompleter completer) {
            INSTANCE.getCommand(name).setTabCompleter(completer);
        }
    }
}
