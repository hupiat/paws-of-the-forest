package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public abstract class MessagesConf extends AbstractConfiguration {

    public static final String CONFIG_FILE_NAME = "messages_config.properties";

    public static final String GENERIC_ERROR =
            getPropertyOrDefault("generic_error", "Unknown usage :", CONFIG_FILE_NAME);

    public static class Chats {
        public static final ChatColor COLOR_PLAYER_NAME_DEFAULT =
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
                getPropertyOrDefault("chats.notAClanMember", "You are not a member of a Clan.", CONFIG_FILE_NAME);

        public static final String NOT_SHOWING_ROLEPLAY =
                getPropertyOrDefault("chats.notShowingRoleplay", "You have disabled roleplay messages.", CONFIG_FILE_NAME);

        public static final String PLAYER_NOT_FOUND =
                getPropertyOrDefault("chats.playerNotFound", "Player specified is offline or doesn't exist.", CONFIG_FILE_NAME);

        public static final String CHAT_TOGGLED =
                getPropertyOrDefault("chats.chatToggled", "Chat toggled :", CONFIG_FILE_NAME);

        public static String getColorName(Player player) {
            try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                if (entity.getClan() == null) {
                    return COLOR_PLAYER_NAME_DEFAULT.toString();
                }
                return entity.getClan().getColorCode();
            }
        }

    }

    public static class Preys {
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("preys.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        public static final String XP_EARNED =
                getPropertyOrDefault("preys.xpEarned", "XP : +", CONFIG_FILE_NAME);

        public static final String XP_LEFT =
                getPropertyOrDefault("preys.xpLeft", "Your total xp is :", CONFIG_FILE_NAME);

        public static final String COINS_EARNED =
                getPropertyOrDefault("preys.coinsEarned", "Paw coins : +", CONFIG_FILE_NAME);

        public static final String COINS_LEFT =
                getPropertyOrDefault("preys.coinsLeft", "Your total Paw coins is :", CONFIG_FILE_NAME);

        public static final String NOT_ENOUGH_COINS =
                getPropertyOrDefault("preys.notEnoughCoins", "You have not enough coins to buy this !", CONFIG_FILE_NAME);

        public static final String MADE_BUY =
                getPropertyOrDefault("preys.madeBuy", "You have bought a shop item for :", CONFIG_FILE_NAME);
    }

    public static class Clans {
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("clans.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        public static final String PLAYER_NOT_BELONG_TO_CLAN =
                getPropertyOrDefault("clans.playerNotBelongToClan", "This player is not in this Clan !", CONFIG_FILE_NAME);

        public static final String CLAN_NOT_FOUND =
                getPropertyOrDefault("clans.clanNotFound", "Clan specified doesn't exist.", CONFIG_FILE_NAME);

        public static final String CLAN_ADDED =
                getPropertyOrDefault("clans.clanAdded", "You've been added to Clan :", CONFIG_FILE_NAME);

        public static final String CLAN_REMOVED =
                getPropertyOrDefault("clans.clanRemoved", "You've been removed from Clan :", CONFIG_FILE_NAME);
    }
}
