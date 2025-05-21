package org.warriorcats.pawsOfTheForest;

import org.bukkit.plugin.java.JavaPlugin;
import org.warriorcats.pawsOfTheForest.core.chats.commands.CommandGlobalChat;
import org.warriorcats.pawsOfTheForest.core.chats.commands.CommandPrivateMessageChat;
import org.warriorcats.pawsOfTheForest.core.chats.commands.CommandPrivateMessageReplyChat;
import org.warriorcats.pawsOfTheForest.core.chats.commands.CommandRoleplayChat;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public final class PawsOfTheForest extends JavaPlugin {

    @Override
    public void onEnable() {
        // Initializing database
        HibernateUtils.getSessionFactory();
        getLogger().info("Hibernate/MySQL connected.");

        // Registering commands
        this.getCommand("global").setExecutor(new CommandGlobalChat());
        this.getCommand("global").setTabCompleter(new CommandGlobalChat());

        this.getCommand("roleplay").setExecutor(new CommandRoleplayChat());
        this.getCommand("roleplay").setTabCompleter(new CommandRoleplayChat());

        this.getCommand("message").setExecutor(new CommandPrivateMessageChat());
        this.getCommand("message").setTabCompleter(new CommandPrivateMessageChat());

        this.getCommand("reply").setExecutor(new CommandPrivateMessageReplyChat());
        this.getCommand("reply").setTabCompleter(new CommandPrivateMessageReplyChat());
    }

    @Override
    public void onDisable() {
        HibernateUtils.shutdown();
    }
}
