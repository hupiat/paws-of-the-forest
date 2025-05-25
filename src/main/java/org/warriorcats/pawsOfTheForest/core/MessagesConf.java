package org.warriorcats.pawsOfTheForest.core;

import org.bukkit.ChatColor;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;

import java.util.Properties;

public abstract class MessagesConf {

    private static final String CONFIG_FILE_NAME = "messages_config.properties";
    private static Properties source = new Properties();

    static {
        source = FileUtils.load(CONFIG_FILE_NAME, source);
    }

    private static boolean checkForDefaultKey(String key, String defaultValue) {
        if (!source.containsKey(key)) {
            source.setProperty(key, defaultValue);
            FileUtils.store(CONFIG_FILE_NAME, source);
            return false;
        }
        return true;
    }

    private static String getPropertyOrDefault(String key, String defaultValue) {
        if (!checkForDefaultKey(key, defaultValue)) {
            return defaultValue;
        }
        return source.getProperty(key);
    }

    private static ChatColor getPropertyOrDefault(String key, ChatColor defaultValue) {
        String value = defaultValue.name();
        if (!checkForDefaultKey(key, value)) {
            return defaultValue;
        }
        return ChatColor.valueOf(source.getProperty(key));
    }

    public static class Chats {
        public static final ChatColor COLOR_PRIVATE_MESSAGE =
                getPropertyOrDefault("chats.colors.privateMessage", ChatColor.AQUA);
        public static final ChatColor COLOR_PLAYER_NAME =
                getPropertyOrDefault("chats.colors.playerName", ChatColor.DARK_AQUA);
        public static final ChatColor COLOR_MESSAGE =
                getPropertyOrDefault("chats.colors.message", ChatColor.GRAY);
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("chats.colors.feedback", ChatColor.GREEN);
        public static final ChatColor COLOR_STANDARD_CHANNEL =
                getPropertyOrDefault("chats.colors.standardChannel", ChatColor.DARK_GREEN);
        public static final ChatColor COLOR_CLAN_CHANNEL =
                getPropertyOrDefault("chats.colors.clansChannel", ChatColor.DARK_PURPLE);
        public static final ChatColor COLOR_ROLEPLAY_CHANNEL =
                getPropertyOrDefault("chats.colors.roleplayChannel", ChatColor.GOLD);


        public static final String NOT_ENOUGH_PERMISSIONS =
                getPropertyOrDefault("chats.notEnoughPermissions", "You don't have enough permissions to do that.");

        public static final String NOT_A_CLAN_MEMBER =
                getPropertyOrDefault("chats.notAClanMember", "You are not a member of a clan.");

        public static final String NOT_SHOWING_ROLEPLAY =
                getPropertyOrDefault("chats.notShowingRoleplay", "You have disabled roleplay messages.");

        public static final String PLAYER_NOT_FOUND =
                getPropertyOrDefault("chats.playerNotFound", "Player specified is offline or doesn't exist.");

        public static final String CHAT_TOGGLED =
                getPropertyOrDefault("chats.chatToggled", "Chat toggled :");

    }
}
