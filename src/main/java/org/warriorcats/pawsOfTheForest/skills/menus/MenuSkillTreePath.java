package org.warriorcats.pawsOfTheForest.skills.menus;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode
public class MenuSkillTreePath {

    public static final ChatColor COLOR_HIGHLIGHT = ChatColor.GRAY;

    public static final int INDEX_PREY_SENSE = 4;
    public static final int INDEX_HUNTERS_COMPASS = 12;
    public static final int INDEX_LOW_SWEEP = 14;
    public static final int INDEX_SILENT_PAW = 20;
    public static final int INDEX_BLOOD_HUNTER = 22;
    public static final int INDEX_EFFICIENT_KILL = 24;

    public static final int INDEX_LOCATION_AWARENESS = 12;
    public static final int INDEX_PATHFINDING_BOOST = 14;
    public static final int INDEX_TRAIL_MEMORY = 20;
    public static final int INDEX_ENDURANCE_TRAVELER = 22;
    public static final int INDEX_CLIMBERS_GRACE = 24;

    public static final int INDEX_HOLD_ON = 12;
    public static final int INDEX_ON_YOUR_PAWS = 14;
    public static final int INDEX_IRON_HIDE = 20;
    public static final int INDEX_IMMUNE_SYSTEM = 22;
    public static final int INDEX_THICK_COAT = 24;
    public static final int INDEX_HEARTY_APPETITE = 30;
    public static final int INDEX_BEAST_OF_BURDEN = 32;

    public static final int INDEX_HERB_KNOWLEDGE = 12;
    public static final int INDEX_BREW_REMEDY = 14;
    public static final int INDEX_QUICK_GATHERER = 20;
    public static final int INDEX_BOTANICAL_LORE = 22;
    public static final int INDEX_CLEAN_PAWS = 24;

    public static final int INDEX_WELL_FED = 11;
    public static final int INDEX_PAMPERED = 13;
    public static final int INDEX_SHELTERED_MIND = 15;

    public static final int INDEX_TRACKER = 11;
    public static final int INDEX_CRAFTY = 13;
    public static final int INDEX_FLEXIBLE_MORALS = 15;

    public static final int INDEX_AMBUSHER = 11;
    public static final int INDEX_SCAVENGE = 13;
    public static final int INDEX_HARD_KNOCK_LIFE = 15;

    public static final int INDEX_URBAN_NAVIGATION = 11;
    public static final int INDEX_RAT_CATCHER = 13;
    public static final int INDEX_DISEASE_RESISTANCE = 15;

    public static final int INDEX_SPEED_OF_THE_MOOR = 11;
    public static final int INDEX_LIGHTSTEP = 13;
    public static final int INDEX_SHARP_WIND = 15;

    public static final int INDEX_THICK_PELT = 11;
    public static final int INDEX_FOREST_COVER = 13;
    public static final int INDEX_STUNNING_BLOW = 15;

    public static final int INDEX_STRONG_SWIMMER = 11;
    public static final int INDEX_AQUA_BALANCE = 13;
    public static final int INDEX_WATERS_RESILIENCE = 15;

    public static final int INDEX_NIGHTSTALKER = 11;
    public static final int INDEX_TOXIC_CLAWS = 13;
    public static final int INDEX_SILENT_KILL = 15;


    private final Set<ItemStack> activeSkills = new HashSet<>();
    private final SkillBranches branch;
    private String title = "Skill Tree";

    public MenuSkillTreePath(SkillBranches branch) {
        this.branch = branch;
        title = branch.toString() + " " + title;
    }

    @Nullable public static Skills getSkillByIndex(int index, SkillBranches branch) {
        return switch (branch) {
            case HUNTING -> switch (index) {
                case INDEX_PREY_SENSE       -> Skills.PREY_SENSE;
                case INDEX_HUNTERS_COMPASS  -> Skills.HUNTERS_COMPASS;
                case INDEX_LOW_SWEEP        -> Skills.LOW_SWEEP;
                case INDEX_SILENT_PAW       -> Skills.SILENT_PAW;
                case INDEX_BLOOD_HUNTER     -> Skills.BLOOD_HUNTER;
                case INDEX_EFFICIENT_KILL   -> Skills.EFFICIENT_KILL;
                default                     -> null;
            };
            case NAVIGATION -> switch (index) {
                case INDEX_LOCATION_AWARENESS -> Skills.LOCATION_AWARENESS;
                case INDEX_PATHFINDING_BOOST  -> Skills.PATHFINDING_BOOST;
                case INDEX_TRAIL_MEMORY       -> Skills.TRAIL_MEMORY;
                case INDEX_ENDURANCE_TRAVELER -> Skills.ENDURANCE_TRAVELER;
                case INDEX_CLIMBERS_GRACE     -> Skills.CLIMBERS_GRACE;
                default                       -> null;
            };
            case RESILIENCE -> switch (index) {
                case INDEX_HOLD_ON          -> Skills.HOLD_ON;
                case INDEX_ON_YOUR_PAWS     -> Skills.ON_YOUR_PAWS;
                case INDEX_IRON_HIDE        -> Skills.IRON_HIDE;
                case INDEX_IMMUNE_SYSTEM    -> Skills.IMMUNE_SYSTEM;
                case INDEX_THICK_COAT       -> Skills.THICK_COAT;
                case INDEX_HEARTY_APPETITE  -> Skills.HEARTY_APPETITE;
                case INDEX_BEAST_OF_BURDEN  -> Skills.BEAST_OF_BURDEN;
                default                     -> null;
            };
            case HERBALIST -> switch (index) {
                case INDEX_HERB_KNOWLEDGE  -> Skills.HERB_KNOWLEDGE;
                case INDEX_BREW_REMEDY     -> Skills.BREW_REMEDY;
                case INDEX_QUICK_GATHERER  -> Skills.QUICK_GATHERER;
                case INDEX_BOTANICAL_LORE  -> Skills.BOTANICAL_LORE;
                case INDEX_CLEAN_PAWS      -> Skills.CLEAN_PAWS;
                default                    -> null;
            };
            case KITTYPET -> switch (index) {
                case INDEX_WELL_FED         -> Skills.WELL_FED;
                case INDEX_PAMPERED         -> Skills.PAMPERED;
                case INDEX_SHELTERED_MIND   -> Skills.SHELTERED_MIND;
                default                     -> null;
            };
            case LONER -> switch (index) {
                case INDEX_TRACKER          -> Skills.TRACKER;
                case INDEX_CRAFTY           -> Skills.CRAFTY;
                case INDEX_FLEXIBLE_MORALS  -> Skills.FLEXIBLE_MORALS;
                default                     -> null;
            };
            case ROGUE -> switch (index) {
                case INDEX_AMBUSHER         -> Skills.AMBUSHER;
                case INDEX_SCAVENGE         -> Skills.SCAVENGE;
                case INDEX_HARD_KNOCK_LIFE  -> Skills.HARD_KNOCK_LIFE;
                default                     -> null;
            };
            case CITY_CAT -> switch (index) {
                case INDEX_URBAN_NAVIGATION -> Skills.URBAN_NAVIGATION;
                case INDEX_RAT_CATCHER      -> Skills.RAT_CATCHER;
                case INDEX_DISEASE_RESISTANCE -> Skills.DISEASE_RESISTANCE;
                default                      -> null;
            };
            case BREEZE_CLAN -> switch (index) {
                case INDEX_SPEED_OF_THE_MOOR -> Skills.SPEED_OF_THE_MOOR;
                case INDEX_LIGHTSTEP -> Skills.LIGHTSTEP;
                case INDEX_SHARP_WIND -> Skills.SHARP_WIND;
                default -> null;
            };
            case ECHO_CLAN -> switch (index) {
                case INDEX_THICK_PELT -> Skills.THICK_PELT;
                case INDEX_FOREST_COVER -> Skills.FOREST_COVER;
                case INDEX_STUNNING_BLOW -> Skills.STUNNING_BLOW;
                default -> null;
            };
            case CREEK_CLAN -> switch (index) {
                case INDEX_STRONG_SWIMMER -> Skills.STRONG_SWIMMER;
                case INDEX_AQUA_BALANCE -> Skills.AQUA_BALANCE;
                case INDEX_WATERS_RESILIENCE -> Skills.WATERS_RESILIENCE;
                default -> null;
            };
            case SHADE_CLAN -> switch (index) {
                case INDEX_NIGHTSTALKER -> Skills.NIGHTSTALKER;
                case INDEX_TOXIC_CLAWS -> Skills.TOXIC_CLAWS;
                case INDEX_SILENT_KILL -> Skills.SILENT_KILL;
                default -> null;
            };
        };
    }

    public void open(Player player) {
        activeSkills.clear();
        Inventory menu = Bukkit.createInventory(null, 45, title);

        PlayerEntity entity = EventsCore.PLAYER_CACHE.get(player.getUniqueId());

        switch (branch) {
            case HUNTING -> drawHuntingBranch(menu, entity);
            case NAVIGATION -> drawNavigationBranch(menu, entity);
            case RESILIENCE -> drawResilienceBranch(menu, entity);
            case HERBALIST -> drawHerbalistBranch(menu, entity);
            case KITTYPET -> drawKittypetBranch(menu, entity);
            case LONER -> drawLonerBranch(menu, entity);
            case ROGUE -> drawRogueBranch(menu, entity);
            case CITY_CAT -> drawCityCatBranch(menu, entity);
            case BREEZE_CLAN -> drawBreezeClanBranch(menu, entity);
            case ECHO_CLAN -> drawEchoClanBranch(menu, entity);
            case CREEK_CLAN -> drawCreekClanBranch(menu, entity);
            case SHADE_CLAN -> drawShadeClanBranch(menu, entity);
        }

        menu.setItem(MenuSkillTree.INDEX_BACK, createSimpleItem(Material.BARRIER, "§cBack", List.of("§7Return to Skill Tree Menu")));
        menu.setItem(MenuSkillTree.INDEX_SKILLS_POINTS, MenuSkillTree.createSkillPointsItemStack(player));

        player.openInventory(menu);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.7f, 1.2f);
    }

    private void drawHuntingBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_PREY_SENSE, createSkillItem(
                Skills.PREY_SENSE.getIcon(),
                Skills.PREY_SENSE.toString(),
                MessagesConf.Skills.PREY_SENSE_DESCRIPTION,
                entity.hasAbility(Skills.PREY_SENSE),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_HUNTERS_COMPASS, createSkillItem(
                Skills.HUNTERS_COMPASS.getIcon(),
                Skills.HUNTERS_COMPASS.toString(),
                MessagesConf.Skills.HUNTERS_COMPASS_DESCRIPTION,
                entity.hasAbility(Skills.HUNTERS_COMPASS),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_LOW_SWEEP, createSkillItem(
                Skills.LOW_SWEEP.getIcon(),
                Skills.LOW_SWEEP.toString(),
                MessagesConf.Skills.LOW_SWEEP_DESCRIPTION,
                entity.hasAbility(Skills.LOW_SWEEP),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_SILENT_PAW, createTieredItem(
                Skills.SILENT_PAW.getIcon(),
                Skills.SILENT_PAW.toString(),
                MessagesConf.Skills.SILENT_PAW_DESCRIPTION,
                entity.getAbilityPerk(Skills.SILENT_PAW),
                Skills.SILENT_PAW.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_BLOOD_HUNTER, createTieredItem(
                Skills.BLOOD_HUNTER.getIcon(),
                Skills.BLOOD_HUNTER.toString(),
                MessagesConf.Skills.BLOOD_HUNTER_DESCRIPTION,
                entity.getAbilityPerk(Skills.BLOOD_HUNTER),
                Skills.BLOOD_HUNTER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_EFFICIENT_KILL, createTieredItem(
                Skills.EFFICIENT_KILL.getIcon(),
                Skills.EFFICIENT_KILL.toString(),
                MessagesConf.Skills.EFFICIENT_KILL_DESCRIPTION,
                entity.getAbilityPerk(Skills.EFFICIENT_KILL),
                Skills.EFFICIENT_KILL.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawNavigationBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_LOCATION_AWARENESS, createSkillItem(
                Skills.LOCATION_AWARENESS.getIcon(),
                Skills.LOCATION_AWARENESS.toString(),
                MessagesConf.Skills.LOCATION_AWARENESS_DESCRIPTION,
                entity.hasAbility(Skills.LOCATION_AWARENESS),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_PATHFINDING_BOOST, createSkillItem(
                Skills.PATHFINDING_BOOST.getIcon(),
                Skills.PATHFINDING_BOOST.toString(),
                MessagesConf.Skills.PATHFINDING_BOOST_DESCRIPTION,
                entity.hasAbility(Skills.PATHFINDING_BOOST),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_TRAIL_MEMORY, createTieredItem(
                Skills.TRAIL_MEMORY.getIcon(),
                Skills.TRAIL_MEMORY.toString(),
                MessagesConf.Skills.TRAIL_MEMORY_DESCRIPTION,
                entity.getAbilityPerk(Skills.TRAIL_MEMORY),
                Skills.TRAIL_MEMORY.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_ENDURANCE_TRAVELER, createTieredItem(
                Skills.ENDURANCE_TRAVELER.getIcon(),
                Skills.ENDURANCE_TRAVELER.toString(),
                MessagesConf.Skills.ENDURANCE_TRAVELER_DESCRIPTION,
                entity.getAbilityPerk(Skills.ENDURANCE_TRAVELER),
                Skills.ENDURANCE_TRAVELER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_CLIMBERS_GRACE, createTieredItem(
                Skills.CLIMBERS_GRACE.getIcon(),
                Skills.CLIMBERS_GRACE.toString(),
                MessagesConf.Skills.CLIMBERS_GRACE_DESCRIPTION,
                entity.getAbilityPerk(Skills.CLIMBERS_GRACE),
                Skills.CLIMBERS_GRACE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawResilienceBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_HOLD_ON, createSkillItem(
                Skills.HOLD_ON.getIcon(),
                Skills.HOLD_ON.toString(),
                MessagesConf.Skills.HOLD_ON_DESCRIPTION,
                entity.hasAbility(Skills.HOLD_ON),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_ON_YOUR_PAWS, createSkillItem(
                Skills.ON_YOUR_PAWS.getIcon(),
                Skills.ON_YOUR_PAWS.toString(),
                MessagesConf.Skills.ON_YOUR_PAWS_DESCRIPTION,
                entity.hasAbility(Skills.ON_YOUR_PAWS),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_IRON_HIDE, createTieredItem(
                Skills.IRON_HIDE.getIcon(),
                Skills.IRON_HIDE.toString(),
                MessagesConf.Skills.IRON_HIDE_DESCRIPTION,
                entity.getAbilityPerk(Skills.IRON_HIDE),
                Skills.IRON_HIDE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_IMMUNE_SYSTEM, createTieredItem(
                Skills.IMMUNE_SYSTEM.getIcon(),
                Skills.IMMUNE_SYSTEM.toString(),
                MessagesConf.Skills.IMMUNE_SYSTEM_DESCRIPTION,
                entity.getAbilityPerk(Skills.IMMUNE_SYSTEM),
                Skills.IMMUNE_SYSTEM.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_THICK_COAT, createTieredItem(
                Skills.THICK_COAT.getIcon(),
                Skills.THICK_COAT.toString(),
                MessagesConf.Skills.THICK_COAT_DESCRIPTION,
                entity.getAbilityPerk(Skills.THICK_COAT),
                Skills.THICK_COAT.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_HEARTY_APPETITE, createTieredItem(
                Skills.HEARTY_APPETITE.getIcon(),
                Skills.HEARTY_APPETITE.toString(),
                MessagesConf.Skills.HEARTY_APPETITE_DESCRIPTION,
                entity.getAbilityPerk(Skills.HEARTY_APPETITE),
                Skills.HEARTY_APPETITE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_BEAST_OF_BURDEN, createTieredItem(
                Skills.BEAST_OF_BURDEN.getIcon(),
                Skills.BEAST_OF_BURDEN.toString(),
                MessagesConf.Skills.BEAST_OF_BURDEN_DESCRIPTION,
                entity.getAbilityPerk(Skills.BEAST_OF_BURDEN),
                Skills.BEAST_OF_BURDEN.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawHerbalistBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_HERB_KNOWLEDGE, createSkillItem(
                Skills.HERB_KNOWLEDGE.getIcon(),
                Skills.HERB_KNOWLEDGE.toString(),
                MessagesConf.Skills.HERB_KNOWLEDGE_DESCRIPTION,
                entity.hasAbility(Skills.HERB_KNOWLEDGE),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_BREW_REMEDY, createSkillItem(
                Skills.BREW_REMEDY.getIcon(),
                Skills.BREW_REMEDY.toString(),
                MessagesConf.Skills.BREW_REMEDY_DESCRIPTION,
                entity.hasAbility(Skills.BREW_REMEDY),
                SkillBranches.UNLOCK_SKILL
        ));

        menu.setItem(INDEX_QUICK_GATHERER, createTieredItem(
                Skills.QUICK_GATHERER.getIcon(),
                Skills.QUICK_GATHERER.toString(),
                MessagesConf.Skills.QUICK_GATHERER_DESCRIPTION,
                entity.getAbilityPerk(Skills.QUICK_GATHERER),
                Skills.QUICK_GATHERER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_BOTANICAL_LORE, createTieredItem(
                Skills.BOTANICAL_LORE.getIcon(),
                Skills.BOTANICAL_LORE.toString(),
                MessagesConf.Skills.BOTANICAL_LORE_DESCRIPTION,
                entity.getAbilityPerk(Skills.BOTANICAL_LORE),
                Skills.BOTANICAL_LORE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_CLEAN_PAWS, createTieredItem(
                Skills.CLEAN_PAWS.getIcon(),
                Skills.CLEAN_PAWS.toString(),
                MessagesConf.Skills.CLEAN_PAWS_DESCRIPTION,
                entity.getAbilityPerk(Skills.CLEAN_PAWS),
                Skills.CLEAN_PAWS.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawKittypetBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_WELL_FED, createTieredItem(
                Skills.WELL_FED.getIcon(),
                Skills.WELL_FED.toString(),
                MessagesConf.Skills.WELL_FED_DESCRIPTION,
                entity.getAbilityPerk(Skills.WELL_FED),
                Skills.WELL_FED.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_PAMPERED, createTieredItem(
                Skills.PAMPERED.getIcon(),
                Skills.PAMPERED.toString(),
                MessagesConf.Skills.PAMPERED_DESCRIPTION,
                entity.getAbilityPerk(Skills.PAMPERED),
                Skills.PAMPERED.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_SHELTERED_MIND, createTieredItem(
                Skills.SHELTERED_MIND.getIcon(),
                Skills.SHELTERED_MIND.toString(),
                MessagesConf.Skills.SHELTERED_MIND_DESCRIPTION,
                entity.getAbilityPerk(Skills.SHELTERED_MIND),
                Skills.SHELTERED_MIND.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawLonerBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_TRACKER, createTieredItem(
                Skills.TRACKER.getIcon(),
                Skills.TRACKER.toString(),
                MessagesConf.Skills.TRACKER_DESCRIPTION,
                entity.getAbilityPerk(Skills.TRACKER),
                Skills.TRACKER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_CRAFTY, createTieredItem(
                Skills.CRAFTY.getIcon(),
                Skills.CRAFTY.toString(),
                MessagesConf.Skills.CRAFTY_DESCRIPTION,
                entity.getAbilityPerk(Skills.CRAFTY),
                Skills.CRAFTY.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_FLEXIBLE_MORALS, createTieredItem(
                Skills.FLEXIBLE_MORALS.getIcon(),
                Skills.FLEXIBLE_MORALS.toString(),
                MessagesConf.Skills.FLEXIBLE_MORALS_DESCRIPTION,
                entity.getAbilityPerk(Skills.FLEXIBLE_MORALS),
                Skills.FLEXIBLE_MORALS.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawRogueBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_AMBUSHER, createTieredItem(
                Skills.AMBUSHER.getIcon(),
                Skills.AMBUSHER.toString(),
                MessagesConf.Skills.AMBUSHER_DESCRIPTION,
                entity.getAbilityPerk(Skills.AMBUSHER),
                Skills.AMBUSHER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_SCAVENGE, createTieredItem(
                Skills.SCAVENGE.getIcon(),
                Skills.SCAVENGE.toString(),
                MessagesConf.Skills.SCAVENGE_DESCRIPTION,
                entity.getAbilityPerk(Skills.SCAVENGE),
                Skills.SCAVENGE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_HARD_KNOCK_LIFE, createTieredItem(
                Skills.HARD_KNOCK_LIFE.getIcon(),
                Skills.HARD_KNOCK_LIFE.toString(),
                MessagesConf.Skills.HARD_KNOCK_LIFE_DESCRIPTION,
                entity.getAbilityPerk(Skills.HARD_KNOCK_LIFE),
                Skills.HARD_KNOCK_LIFE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawCityCatBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_URBAN_NAVIGATION, createTieredItem(
                Skills.URBAN_NAVIGATION.getIcon(),
                Skills.URBAN_NAVIGATION.toString(),
                MessagesConf.Skills.URBAN_NAVIGATION_DESCRIPTION,
                entity.getAbilityPerk(Skills.URBAN_NAVIGATION),
                Skills.URBAN_NAVIGATION.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_RAT_CATCHER, createTieredItem(
                Skills.RAT_CATCHER.getIcon(),
                Skills.RAT_CATCHER.toString(),
                MessagesConf.Skills.RAT_CATCHER_DESCRIPTION,
                entity.getAbilityPerk(Skills.RAT_CATCHER),
                Skills.RAT_CATCHER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_DISEASE_RESISTANCE, createTieredItem(
                Skills.DISEASE_RESISTANCE.getIcon(),
                Skills.DISEASE_RESISTANCE.toString(),
                MessagesConf.Skills.DISEASE_RESISTANCE_DESCRIPTION,
                entity.getAbilityPerk(Skills.DISEASE_RESISTANCE),
                Skills.DISEASE_RESISTANCE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawBreezeClanBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_SPEED_OF_THE_MOOR, createTieredItem(
                Skills.SPEED_OF_THE_MOOR.getIcon(),
                Skills.SPEED_OF_THE_MOOR.toString(),
                MessagesConf.Skills.SPEED_OF_THE_MOOR_DESCRIPTION,
                entity.getAbilityPerk(Skills.SPEED_OF_THE_MOOR),
                Skills.SPEED_OF_THE_MOOR.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_LIGHTSTEP, createTieredItem(
                Skills.LIGHTSTEP.getIcon(),
                Skills.LIGHTSTEP.toString(),
                MessagesConf.Skills.LIGHTSTEP_DESCRIPTION,
                entity.getAbilityPerk(Skills.LIGHTSTEP),
                Skills.LIGHTSTEP.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_SHARP_WIND, createTieredItem(
                Skills.SHARP_WIND.getIcon(),
                Skills.SHARP_WIND.toString(),
                MessagesConf.Skills.SHARP_WIND_DESCRIPTION,
                entity.getAbilityPerk(Skills.SHARP_WIND),
                Skills.SHARP_WIND.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawEchoClanBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_THICK_PELT, createTieredItem(
                Skills.THICK_PELT.getIcon(),
                Skills.THICK_PELT.toString(),
                MessagesConf.Skills.THICK_PELT_DESCRIPTION,
                entity.getAbilityPerk(Skills.THICK_PELT),
                Skills.THICK_PELT.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_FOREST_COVER, createTieredItem(
                Skills.FOREST_COVER.getIcon(),
                Skills.FOREST_COVER.toString(),
                MessagesConf.Skills.FOREST_COVER_DESCRIPTION,
                entity.getAbilityPerk(Skills.FOREST_COVER),
                Skills.FOREST_COVER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_STUNNING_BLOW, createTieredItem(
                Skills.STUNNING_BLOW.getIcon(),
                Skills.STUNNING_BLOW.toString(),
                MessagesConf.Skills.STUNNING_BLOW_DESCRIPTION,
                entity.getAbilityPerk(Skills.STUNNING_BLOW),
                Skills.STUNNING_BLOW.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawCreekClanBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_STRONG_SWIMMER, createTieredItem(
                Skills.STRONG_SWIMMER.getIcon(),
                Skills.STRONG_SWIMMER.toString(),
                MessagesConf.Skills.STRONG_SWIMMER_DESCRIPTION,
                entity.getAbilityPerk(Skills.STRONG_SWIMMER),
                Skills.STRONG_SWIMMER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_AQUA_BALANCE, createTieredItem(
                Skills.AQUA_BALANCE.getIcon(),
                Skills.AQUA_BALANCE.toString(),
                MessagesConf.Skills.AQUA_BALANCE_DESCRIPTION,
                entity.getAbilityPerk(Skills.AQUA_BALANCE),
                Skills.AQUA_BALANCE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_WATERS_RESILIENCE, createTieredItem(
                Skills.WATERS_RESILIENCE.getIcon(),
                Skills.WATERS_RESILIENCE.toString(),
                MessagesConf.Skills.WATERS_RESILIENCE_DESCRIPTION,
                entity.getAbilityPerk(Skills.WATERS_RESILIENCE),
                Skills.WATERS_RESILIENCE.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private void drawShadeClanBranch(Inventory menu, PlayerEntity entity) {
        menu.setItem(INDEX_NIGHTSTALKER, createTieredItem(
                Skills.NIGHTSTALKER.getIcon(),
                Skills.NIGHTSTALKER.toString(),
                MessagesConf.Skills.NIGHTSTALKER_DESCRIPTION,
                entity.getAbilityPerk(Skills.NIGHTSTALKER),
                Skills.NIGHTSTALKER.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_TOXIC_CLAWS, createTieredItem(
                Skills.TOXIC_CLAWS.getIcon(),
                Skills.TOXIC_CLAWS.toString(),
                MessagesConf.Skills.TOXIC_CLAWS_DESCRIPTION,
                entity.getAbilityPerk(Skills.TOXIC_CLAWS),
                Skills.TOXIC_CLAWS.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));

        menu.setItem(INDEX_SILENT_KILL, createTieredItem(
                Skills.SILENT_KILL.getIcon(),
                Skills.SILENT_KILL.toString(),
                MessagesConf.Skills.SILENT_KILL_DESCRIPTION,
                entity.getAbilityPerk(Skills.SILENT_KILL),
                Skills.SILENT_KILL.getMaxTiers(),
                SkillBranches.UNLOCK_SKILL_TIER
        ));
    }

    private ItemStack createSkillItem(Material material, String name, String desc, boolean unlocked, double xpCost) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(unlocked ? "§a" + name : "§e" + name);
        meta.setLore(List.of(
                COLOR_HIGHLIGHT + desc,
                "",
                unlocked ? "§aUnlocked" : "§6Cost: " + xpCost + " XP levels",
                unlocked ? "" : "§eClick to unlock"
        ));
        item.setItemMeta(meta);

        activeSkills.add(item);

        return item;
    }

    private ItemStack createTieredItem(Material material, String name, String desc, double currentXp, int maxTier, double xpPerTier) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        int currentTier = (int) Math.round(currentXp / xpPerTier);

        String display = (currentTier >= maxTier ? "§a" : "§e") + name + COLOR_HIGHLIGHT + " (Tier " + currentTier + "/" + maxTier + ")";
        meta.setDisplayName(display);
        meta.setLore(List.of(
                COLOR_HIGHLIGHT + desc,
                "",
                currentTier >= maxTier
                        ? "§aMax Tier Reached"
                        : "§6Next Tier Cost: " + xpPerTier + " XP levels",
                currentTier >= maxTier ? "" : "§eClick to upgrade"
        ));

        item.setItemMeta(meta);
        return item;
    }

    private static ItemStack createSimpleItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
