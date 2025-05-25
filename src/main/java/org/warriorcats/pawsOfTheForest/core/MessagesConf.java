package org.warriorcats.pawsOfTheForest.core;

import org.bukkit.ChatColor;

public abstract class MessagesConf {

    private static String getPropertyOrDefault(String key, String defaultValue) {
        return defaultValue;
    }

    public static class Chats {
        public static final ChatColor COLOR_PRIVATE_MESSAGE = ChatColor.AQUA;
        public static final ChatColor COLOR_PLAYER_NAME = ChatColor.DARK_AQUA;
        public static final ChatColor COLOR_MESSAGE = ChatColor.GRAY;
        public static final ChatColor COLOR_FEEDBACK = ChatColor.GREEN;
        public static final ChatColor COLOR_STANDARD_CHANNEL = ChatColor.DARK_GREEN;
        public static final ChatColor COLOR_CLAN_CHANNEL = ChatColor.DARK_PURPLE;
        public static final ChatColor COLOR_ROLEPLAY_CHANNEL = ChatColor.GOLD;


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
