package org.warriorcats.pawsOfTheForest.core.chats;

import org.warriorcats.pawsOfTheForest.utils.EnumsUtils;

public enum ChatChannels {
    GLOBAL, LOCAL, CLAN, ROLEPLAY, LOCALROLEPLAY;

    public static final ChatChannels DEFAULT_TOGGLED = GLOBAL;
    public static final int LOCAL_CHANNEL_RADIUS = 50;

    public static ChatChannels from(String channelStr) {
        return EnumsUtils.from(channelStr, ChatChannels.class);
    }

    @Override
    public String toString() {
        if (this == LOCALROLEPLAY) {
            return "LOCAL ROLEPLAY";
        }
        return super.toString();
    }

    public static boolean isRoleplay(ChatChannels channel) {
        return channel == ROLEPLAY || channel == LOCALROLEPLAY;
    }
}
