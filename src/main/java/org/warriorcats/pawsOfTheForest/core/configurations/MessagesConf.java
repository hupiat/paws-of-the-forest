package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.ChatColor;

public abstract class MessagesConf extends AbstractConfiguration {

    private static final String CONFIG_FILE_NAME = "messages_config.properties";

    static {
        loadPropertiesSource(CONFIG_FILE_NAME);
    }

    public static class Chats {
        public static final ChatColor COLOR_PRIVATE_MESSAGE =
                getPropertyOrDefault("chats.colors.privateMessage", ChatColor.AQUA, CONFIG_FILE_NAME);
        public static final ChatColor COLOR_PLAYER_NAME =
                getPropertyOrDefault("chats.colors.playerName", ChatColor.DARK_AQUA, CONFIG_FILE_NAME);
        public static final ChatColor COLOR_MESSAGE =
                getPropertyOrDefault("chats.colors.message", ChatColor.GRAY, CONFIG_FILE_NAME);
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("chats.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);
        public static final ChatColor COLOR_STANDARD_CHANNEL =
                getPropertyOrDefault("chats.colors.standardChannel", ChatColor.DARK_GREEN, CONFIG_FILE_NAME);
        public static final ChatColor COLOR_CLAN_CHANNEL =
                getPropertyOrDefault("chats.colors.clansChannel", ChatColor.DARK_PURPLE, CONFIG_FILE_NAME);
        public static final ChatColor COLOR_ROLEPLAY_CHANNEL =
                getPropertyOrDefault("chats.colors.roleplayChannel", ChatColor.GOLD, CONFIG_FILE_NAME);


        public static final String NOT_ENOUGH_PERMISSIONS =
                getPropertyOrDefault("chats.notEnoughPermissions", "You don't have enough permissions to do that.", CONFIG_FILE_NAME);

        public static final String NOT_A_CLAN_MEMBER =
                getPropertyOrDefault("chats.notAClanMember", "You are not a member of a clan.", CONFIG_FILE_NAME);

        public static final String NOT_SHOWING_ROLEPLAY =
                getPropertyOrDefault("chats.notShowingRoleplay", "You have disabled roleplay messages.", CONFIG_FILE_NAME);

        public static final String PLAYER_NOT_FOUND =
                getPropertyOrDefault("chats.playerNotFound", "Player specified is offline or doesn't exist.", CONFIG_FILE_NAME);

        public static final String CHAT_TOGGLED =
                getPropertyOrDefault("chats.chatToggled", "Chat toggled :", CONFIG_FILE_NAME);

    }

    public static class Preys {
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("preys.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        public static final String XP_EARNED =
                getPropertyOrDefault("preys.xpEarned", "You have killed a prey ! You have earned xp :", CONFIG_FILE_NAME);

        public static final String COINS_EARNED =
                getPropertyOrDefault("preys.coinsEarned", "You have killed a prey ! You have earned coins :", CONFIG_FILE_NAME);
    }
}
