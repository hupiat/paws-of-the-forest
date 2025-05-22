package org.warriorcats.pawsOfTheForest.chats;

public enum ChatChannel {
    GLOBAL, LOCAL, CLAN, ROLEPLAY, LOCALROLEPLAY;

    public static final ChatChannel DEFAULT_TOGGLED = GLOBAL;
    public static final int LOCAL_CHANNEL_RADIUS = 50;
}
