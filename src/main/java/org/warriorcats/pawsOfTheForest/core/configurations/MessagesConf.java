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

        public static final String BREEZE_CLAN_DESCRIPTION =
                getPropertyOrDefault("skills.breezeClan.description", "Skills from the fast BreezeClan.", CONFIG_FILE_NAME);

        public static final String ECHO_CLAN_DESCRIPTION =
                getPropertyOrDefault("skills.echoClan.description", "Skills from the stealthy EchoClan.", CONFIG_FILE_NAME);

        public static final String CREEK_CLAN_DESCRIPTION =
                getPropertyOrDefault("skills.creekClan.description", "Skills from the water-wise CreekClan.", CONFIG_FILE_NAME);

        public static final String SHADE_CLAN_DESCRIPTION =
                getPropertyOrDefault("skills.shadeClan.description", "Skills from the elusive ShadeClan.", CONFIG_FILE_NAME);

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

        public static final String PLAYER_MESSAGE_BLEEDING =
                getPropertyOrDefault("skills.playerMessages.bleeding", "Sharp Wind ! You are bleeding !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_BLEEDING =
                getPropertyOrDefault("skills.playerMessages.appliedBleeding", "Sharp Wind ! You have applied bleeding !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_STAGGERED =
                getPropertyOrDefault("skills.playerMessages.staggered", "Stunning Blow ! You are staggered !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_STAGGERED =
                getPropertyOrDefault("skills.playerMessages.appliedStaggered", "Stunning Blow ! You have applied stagger !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_POISONED =
                getPropertyOrDefault("skills.playerMessages.poisoned", "Toxic Claws ! You are poisoned !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_POISONED =
                getPropertyOrDefault("skills.playerMessages.appliedPoisoned", "Toxic Claws ! You have applied poison !", CONFIG_FILE_NAME);

        public static final String PLAYER_MESSAGE_APPLIED_AQUA_BALANCE =
                getPropertyOrDefault("skills.playerMessages.appliedAquaBalance", "Aqua Balance ! You have caught some fresh fish !", CONFIG_FILE_NAME);

        public static final String PREY_SENSE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.preySense", "Reveal nearby prey (5s glowing, 25 blocks)", CONFIG_FILE_NAME);

        public static final String HUNTERS_COMPASS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.huntersCompass", "Points to closest huntable target (updates every 60s)", CONFIG_FILE_NAME);

        public static final String LOW_SWEEP_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.lowSweep", "Applies Slowness II to target (2.5s)", CONFIG_FILE_NAME);

        public static final String SILENT_PAW_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.silentPaw", "Reduces movement sound radius", CONFIG_FILE_NAME);

        public static final String BLOOD_HUNTER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.bloodHunter", "Higher chance for quality prey", CONFIG_FILE_NAME);

        public static final String EFFICIENT_KILL_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.efficientKill", "More XP/food on stealth kills", CONFIG_FILE_NAME);

        public static final String LOCATION_AWARENESS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.locationAwareness", "Cycle compass between known waypoints", CONFIG_FILE_NAME);

        public static final String PATHFINDING_BOOST_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.pathfindingBoost", "Grants Speed I and Jump I outside combat", CONFIG_FILE_NAME);

        public static final String TRAIL_MEMORY_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.trailMemory", "Recall landmarks instantly", CONFIG_FILE_NAME);

        public static final String ENDURANCE_TRAVELER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.enduranceTraveler", "Reduce hunger loss out of combat", CONFIG_FILE_NAME);

        public static final String CLIMBERS_GRACE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.climbersGrace", "Jump higher passively", CONFIG_FILE_NAME);

        public static final String HOLD_ON_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.holdOn", "Avoids death and enters downed state", CONFIG_FILE_NAME);

        public static final String ON_YOUR_PAWS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.onYourPaws", "Revive downed ally after 8s", CONFIG_FILE_NAME);

        public static final String IRON_HIDE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.ironHide", "+1 armor per tier", CONFIG_FILE_NAME);

        public static final String IMMUNE_SYSTEM_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.immuneSystem", "10% illness resistance per tier", CONFIG_FILE_NAME);

        public static final String THICK_COAT_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.thickCoat", "Cold resistance, weak to fire", CONFIG_FILE_NAME);

        public static final String HEARTY_APPETITE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.heartyAppetite", "Increases food saturation restoration per tier", CONFIG_FILE_NAME);

        public static final String BEAST_OF_BURDEN_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.beastOfBurden", "Adds inventory capacity per tier", CONFIG_FILE_NAME);

        public static final String HERB_KNOWLEDGE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.herbKnowledge", "Highlights herbs within 15 blocks", CONFIG_FILE_NAME);

        public static final String BREW_REMEDY_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.brewRemedy", "Brew cures using collected herbs", CONFIG_FILE_NAME);

        public static final String QUICK_GATHERER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.quickGatherer", "Collect herbs faster", CONFIG_FILE_NAME);

        public static final String BOTANICAL_LORE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.botanicalLore", "Unlock new recipes or uses", CONFIG_FILE_NAME);

        public static final String CLEAN_PAWS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.cleanPaws", "Reduce self-infection risk", CONFIG_FILE_NAME);

        public static final String WELL_FED_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.wellFed", "Heals faster when full.", CONFIG_FILE_NAME);

        public static final String PAMPERED_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.pampered", "Less likely to fall ill.", CONFIG_FILE_NAME);

        public static final String SHELTERED_MIND_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.shelteredMind", "Immune to fear effects.", CONFIG_FILE_NAME);

        public static final String TRACKER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.tracker", "Detect recent footsteps.", CONFIG_FILE_NAME);

        public static final String CRAFTY_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.crafty", "Use herbs more efficiently.", CONFIG_FILE_NAME);

        public static final String FLEXIBLE_MORALS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.flexibleMorals", "Can trade/steal from NPCs.", CONFIG_FILE_NAME);

        public static final String AMBUSHER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.ambusher", "+Sneak attack damage.", CONFIG_FILE_NAME);

        public static final String SCAVENGE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.scavenge", "Loot items from trash piles.", CONFIG_FILE_NAME);

        public static final String HARD_KNOCK_LIFE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.hardKnockLife", "+1 natural armor.", CONFIG_FILE_NAME);

        public static final String URBAN_NAVIGATION_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.urbanNavigation", "Speed boost on concrete/stone.", CONFIG_FILE_NAME);

        public static final String RAT_CATCHER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.ratCatcher", "Track and catch rats.", CONFIG_FILE_NAME);

        public static final String DISEASE_RESISTANCE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.diseaseResistance", "Reduced illness severity.", CONFIG_FILE_NAME);

        public static final String SPEED_OF_THE_MOOR_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.speedOfTheMoor", "+15% plains movement speed.", CONFIG_FILE_NAME);

        public static final String LIGHTSTEP_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.lightstep", "Reduced fall damage.", CONFIG_FILE_NAME);

        public static final String SHARP_WIND_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.sharpWind", "10% chance to bleed in open spaces.", CONFIG_FILE_NAME);

        public static final String THICK_PELT_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.thickPelt", "Reduces melee damage.", CONFIG_FILE_NAME);

        public static final String FOREST_COVER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.forestCover", "Camouflage in wooded biomes.", CONFIG_FILE_NAME);

        public static final String STUNNING_BLOW_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.stunningBlow", "Bonus stagger chance from elevated attacks.", CONFIG_FILE_NAME);

        public static final String STRONG_SWIMMER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.strongSwimmer", "Faster water movement.", CONFIG_FILE_NAME);

        public static final String AQUA_BALANCE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.aquaBalance", "Can fish for food.", CONFIG_FILE_NAME);

        public static final String WATERS_RESILIENCE_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.watersResilience", "Hunger decays slower in wet zones.", CONFIG_FILE_NAME);

        public static final String NIGHTSTALKER_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.nightstalker", "No night blindness.", CONFIG_FILE_NAME);

        public static final String TOXIC_CLAWS_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.toxicClaws", "Poison on low-light hits.", CONFIG_FILE_NAME);

        public static final String SILENT_KILL_DESCRIPTION =
                getPropertyOrDefault("skills.descriptions.silentKill", "Bonus damage on sneak attacks.", CONFIG_FILE_NAME);

    }
}
