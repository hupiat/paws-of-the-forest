package org.warriorcats.pawsOfTheForest;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.warriorcats.pawsOfTheForest.clans.CommandClans;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.chats.commands.*;
import org.warriorcats.pawsOfTheForest.core.commands.CommandList;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.illnesses.EventsIllnesses;
import org.warriorcats.pawsOfTheForest.shops.CommandOpenShop;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.PreysConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.core.settings.EventsSettings;
import org.warriorcats.pawsOfTheForest.preys.EventsPreys;
import org.warriorcats.pawsOfTheForest.shops.EventsShop;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsActives;
import org.warriorcats.pawsOfTheForest.skills.commands.CommandOpenBackpack;
import org.warriorcats.pawsOfTheForest.skills.commands.CommandOpenSkills;
import org.warriorcats.pawsOfTheForest.skills.EventsSkillsPassives;
import org.warriorcats.pawsOfTheForest.skills.menus.EventsSkillsMenu;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.HttpServerUtils;
import org.warriorcats.pawsOfTheForest.vitals.EventsVitals;

import java.nio.file.Path;
import java.nio.file.Paths;

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
        MessagesConf.getInstance().load(MessagesConf.CONFIG_FILE_NAME);
        ShopsConf.getInstance().load(ShopsConf.CONFIG_FILE_NAME);
        PreysConf.getInstance().load(PreysConf.CONFIG_FILE_NAME);

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
        registerCommand("shop", new CommandOpenShop());
        registerCommand("clans", new CommandClans());
        registerCommand("skills", new CommandOpenSkills());
        registerCommand("backpack", new CommandOpenBackpack());

        // Registering events
        registerEvent(new EventsCore());
        registerEvent(new EventsSettings());
        registerEvent(new EventsShop());
        registerEvent(new EventsPreys());
        registerEvent(new EventsSkillsPassives());
        registerEvent(new EventsSkillsMenu());
        registerEvent(new EventsSkillsActives());
        registerEvent(new EventsVitals());
        registerEvent(new EventsIllnesses());

        // Zipping resources pack to be sent to all players, and serving in local
        prepareHttpServerForResourcesPack();
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

    private void registerEvent(Listener instance) {
        INSTANCE.getServer().getPluginManager().registerEvents(instance, INSTANCE);
        if (instance instanceof LoadingListener loadingListener) {
            loadingListener.load();
        }
    }

    private void prepareHttpServerForResourcesPack() {
        Path pluginData = FileUtils.PLUGIN_DATA_FOLDER.toPath();
        Path modelEngineFolder = Paths.get("plugins", "ModelEngine", "resource pack");
        Path tmpUnzipFolder = pluginData.resolve("tmp_base_pack");
        Path baseZip = pluginData.resolve("base_pack.zip");
        Path mergedPackZip = pluginData.resolve(FileUtils.RESOURCES_PACK_PATH);

        FileUtils.unzipFolder(baseZip, tmpUnzipFolder);
        FileUtils.copyFolder(modelEngineFolder, tmpUnzipFolder);

        FileUtils.zipFolder(tmpUnzipFolder, mergedPackZip);

        FileUtils.deleteFolder(tmpUnzipFolder);

        HttpServerUtils.start(
                HttpServerUtils.RESOURCES_PACK_PORT,
                mergedPackZip,
                "/" + FileUtils.RESOURCES_PACK_PATH
        );
    }
}
