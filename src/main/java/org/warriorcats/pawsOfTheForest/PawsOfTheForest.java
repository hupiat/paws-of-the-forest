package org.warriorcats.pawsOfTheForest;

import org.bukkit.plugin.java.JavaPlugin;
import org.warriorcats.pawsOfTheForest.chats.commands.*;
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

        // Registering commands
        this.getCommand("global").setExecutor(new CommandGlobalChat());
        this.getCommand("global").setTabCompleter(new CommandGlobalChat());

        this.getCommand("local").setExecutor(new CommandLocalChat());
        this.getCommand("local").setTabCompleter(new CommandLocalChat());

        this.getCommand("clan").setExecutor(new CommandClanChat());
        this.getCommand("clan").setTabCompleter(new CommandClanChat());

        this.getCommand("roleplay").setExecutor(new CommandRoleplayChat());
        this.getCommand("roleplay").setTabCompleter(new CommandRoleplayChat());

        this.getCommand("localroleplay").setExecutor(new CommandLocalRoleplayChat());
        this.getCommand("localroleplay").setTabCompleter(new CommandLocalRoleplayChat());

        this.getCommand("message").setExecutor(new CommandPrivateMessageChat());
        this.getCommand("message").setTabCompleter(new CommandPrivateMessageChat());

        this.getCommand("reply").setExecutor(new CommandPrivateMessageReplyChat());
        this.getCommand("reply").setTabCompleter(new CommandPrivateMessageReplyChat());

        this.getCommand("toggle").setExecutor(new CommandToggleChat());
        this.getCommand("toggle").setTabCompleter(new CommandToggleChat());
    }

    @Override
    public void onDisable() {
        HibernateUtils.shutdown();
    }
}
