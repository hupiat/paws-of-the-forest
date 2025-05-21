package org.warriorcats.pawsOfTheForest.core;

public abstract class MessagesConf {

    private static String getPropertyOrDefault(String key, String defaultValue) {
        return defaultValue;
    }

    public static class Chats {
        public static final String NOT_ENOUGH_PERMISSIONS =
                getPropertyOrDefault("chats.notEnoughPermissions", "You don't have enough permissions to do that.");

        public static final String PLAYER_NOT_FOUND =
                getPropertyOrDefault("chats.playerNotFound", "Player specified is offline or doesn't exist.");

        public static final String MESSAGE_SENT =
                getPropertyOrDefault("chats.messageSent", "Message sent to :");

        public static final String MESSAGE_SENT_GLOBAL =
                getPropertyOrDefault("chats.messageSentGlobal", "Message sent to all players.");

        public static final String MESSAGE_SENT_ROLEPLAY =
                getPropertyOrDefault("chats.messageSentRolePlay", "Message sent to all players in roleplay.");
    }
}
