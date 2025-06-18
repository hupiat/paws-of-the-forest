package org.warriorcats.pawsOfTheForest.core.configurations;

import org.bukkit.ChatColor;

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
    }

    public static class Preys {
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("preys.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        public static final String SKILL_POINTS_EARNED =
                getPropertyOrDefault("preys.skillPointsEarned", "Skill points : +", CONFIG_FILE_NAME);

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

    public static class Skills {
        public static final ChatColor COLOR_FEEDBACK =
                getPropertyOrDefault("skills.colors.feedback", ChatColor.GREEN, CONFIG_FILE_NAME);

        public static final ChatColor COLOR_DESCRIPTION =
                getPropertyOrDefault("skills.colors.description", ChatColor.WHITE, CONFIG_FILE_NAME);

        public static final String HUNTING_DESCRIPTION =
                getPropertyOrDefault("skills.hunting.description", "Track your prey, unlock primal instincts.", CONFIG_FILE_NAME);

        public static final String NAVIGATION_DESCRIPTION =
                getPropertyOrDefault("skills.navigation.description", "Master movement and memory of paths.", CONFIG_FILE_NAME);

        public static final String RESILIENCE_DESCRIPTION =
                getPropertyOrDefault("skills.resilience.description", "Survive harder hits, help your clanmates.", CONFIG_FILE_NAME);

        public static final String HERBALIST_DESCRIPTION =
                getPropertyOrDefault("skills.herbalist.description", "Use herbs to heal, resist illness, and brew.", CONFIG_FILE_NAME);

        public static final String KITTYPET_DESCRIPTION =
                getPropertyOrDefault("skills.kittypet.description", "A cat raised in the warmth and comfort of Twoleg dens. Well-fed, pampered, and protected, but distant from the wild ways of the forest.", CONFIG_FILE_NAME);

        public static final String LONER_DESCRIPTION =
                getPropertyOrDefault("skills.loner.description", "A solitary wanderer who shuns Clans and Twolegs alike. Living by their own rules, trusting no one but themselves.", CONFIG_FILE_NAME);

        public static final String ROGUE_DESCRIPTION =
                getPropertyOrDefault("skills.rogue.description", "A fierce outcast, untamed and unpredictable. Rogues survive by tooth and claw, often causing trouble near Clan borders.", CONFIG_FILE_NAME);

        public static final String CITY_CAT_DESCRIPTION =
                getPropertyOrDefault("skills.cityCat.description", "A streetwise feline, navigating alleyways and rooftops. Cunning and adaptable, they thrive in the bustling chaos of the Twolegplace.", CONFIG_FILE_NAME);

        public static final String MENU_EXIT =
                getPropertyOrDefault("skills.menu.exit", "Exit this menu.", CONFIG_FILE_NAME);

        public static final String MENU_SKILL_POINTS =
                getPropertyOrDefault("skills.menu.skillPoints", "You have skill points :", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_NOT_ENOUGH_POINTS =
                getPropertyOrDefault("skills.playerMessages.notEnoughPoints", "You have not enough points to buy this !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_ALREADY_UNLOCKED =
                getPropertyOrDefault("skills.playerMessages.alreadyUnlocked", "You have already unlocked this ! (or you have reached max level)", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_BEAST_OF_BURDEN_NOT_UNLOCKED =
                getPropertyOrDefault("skills.playerMessages.beastOfBurdenNotUnlocked", "You don't have the Beast of Burden skill.", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_STOLE_FROM_NPC =
                getPropertyOrDefault("skills.playerMessages.stoleFromNPC", "You discreetly stole from NPC !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_FOUND_TRASH_LOOT =
                getPropertyOrDefault("skills.playerMessages.foundTrashLoot", "You scavenged some trash and found a loot !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_CAUGHT_RAT =
                getPropertyOrDefault("skills.playerMessages.caughtRat", "You have caught a rat !", CONFIG_FILE_NAME);
    }
}
