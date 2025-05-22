package org.warriorcats.pawsOfTheForest.core;

import org.bukkit.ChatColor;
import org.warriorcats.pawsOfTheForest.chats.ChatChannel;

public abstract class MessagesConf {

    private static String getPropertyOrDefault(String key, String defaultValue) {
        return defaultValue;
    }

    public static class Chats {
        public static final ChatColor COLOR_PRIVATE_MESSAGE = ChatColor.AQUA;
        public static final ChatColor COLOR_SENDER = ChatColor.WHITE;
        public static final ChatColor COLOR_MESSAGE = ChatColor.GRAY;
        public static final ChatColor COLOR_FEEDBACK = ChatColor.GREEN;
        public static final ChatColor COLOR_STANDARD_CHANNEL = ChatColor.BLACK;
        public static final ChatColor COLOR_CLAN_CHANNEL = ChatColor.DARK_PURPLE;
        public static final ChatColor COLOR_ROLEPLAY_CHANNEL = ChatColor.GOLD;


        public static final String NOT_ENOUGH_PERMISSIONS =
                getPropertyOrDefault("chats.notEnoughPermissions", "You don't have enough permissions to do that.");

        public static final String PLAYER_NOT_FOUND =
                getPropertyOrDefault("chats.playerNotFound", "Player specified is offline or doesn't exist.");

        public static final String MESSAGE_SENT =
                getPropertyOrDefault("chats.messageSent", "Message sent to :");

        public static final String MESSAGE_SENT_GLOBAL =
                getPropertyOrDefault("chats.messageSentGlobal", "Message sent to all players.");

        public static final String MESSAGE_SENT_LOCAL =
                getPropertyOrDefault("chats.messageSentLocal", "Message sent to players within " + ChatChannel.LOCAL_CHANNEL_RADIUS + " blocks.");

        public static final String MESSAGE_SENT_ROLEPLAY =
                getPropertyOrDefault("chats.messageSentRolePlay", "Message sent to all players in roleplay.");

        public static final String MESSAGE_SENT_LOCAL_ROLEPLAY =
                getPropertyOrDefault("chats.messageSentLocalRolePlay", "Message sent to players within " + ChatChannel.LOCAL_CHANNEL_RADIUS + " blocks in roleplay.");

        public static final String MESSAGE_SENT_CLAN =
                getPropertyOrDefault("chats.messageSentClan", "Message sent to all players in clan.");
    }
}
