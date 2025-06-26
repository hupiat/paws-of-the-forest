package org.warriorcats.pawsOfTheForest.skills.menus;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillEntity;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.ItemsUtils;
import org.warriorcats.pawsOfTheForest.utils.PlayersUtils;
import org.warriorcats.pawsOfTheForest.utils.SkillsUtils;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventsSkillsMenu implements Listener {

    public static final Map<UUID, MenuSkillTreePath> MENUS_OPENED = new HashMap<>();
    public static final Map<UUID, List<MenuSkillTreePath>> MENUS_STORED = new HashMap<>();

    public static boolean hasMenuSkillTreePathByPlayer(Player player, SkillBranches branches) {
        try {
            return getMenuSkillTreePathByPlayer(player, branches) != null;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public static MenuSkillTreePath getMenuSkillTreePathByPlayer(Player player, SkillBranches branch) {
        return MENUS_STORED.get(player.getUniqueId()).stream()
                .filter(menu -> menu.getBranch() == branch)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("Could not find stored menu skill tree path for player and branch : " + player.getName() + ", " + branch.toString()));
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (EventsCore.PLAYERS_LEAVING.contains(player)) return;

        if (!event.getView().getTitle().equals(MenuBackpack.TITLE)) return;

        HibernateUtils.withTransaction(((transaction, session) -> {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());

            ItemStack[] contents = event.getInventory().getContents();
            boolean empty = true;
            for (ItemStack item : contents) {
                if (!ItemsUtils.isEmpty(item)) {
                    empty = false;
                    break;
                }
            }

            if (empty) {
                entity.setBackpackData(null);
            } else {
                entity.setBackpackData(ItemsUtils.serializeItemStackArray(contents));
            }
            return entity;
        }));
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        int index = event.getSlot();

        if (event.getView().getTitle().equals(MenuSkillTree.TITLE)) {
            event.setCancelled(true);
            handleMainMenuClick(index, player);
            return;
        }

        MenuSkillTreePath openedMenu = MENUS_OPENED.get(player.getUniqueId());
        if (openedMenu != null && event.getView().getTitle().equals(openedMenu.getTitle())) {
            event.setCancelled(true);
            handlePerksMenuClick(index, player);
        }
    }

    private void handleMainMenuClick(int index, Player player) {
        Consumer<SkillBranches> consumer = branch -> {
            player.closeInventory();
            MenuSkillTreePath menuSkillTreePath = new MenuSkillTreePath(branch);
            MENUS_OPENED.put(player.getUniqueId(), menuSkillTreePath);
            MENUS_STORED.computeIfAbsent(player.getUniqueId(), uuid -> new ArrayList<>());
            MENUS_STORED.get(player.getUniqueId()).removeIf(menu -> menu.getBranch() == branch);
            MENUS_STORED.get(player.getUniqueId()).add(menuSkillTreePath);
            MENUS_OPENED.get(player.getUniqueId()).open(player);
        };

        switch (index) {
            case MenuSkillTree.INDEX_HUNTING:
                consumer.accept(SkillBranches.HUNTING);
                break;

            case MenuSkillTree.INDEX_NAVIGATION:
                consumer.accept(SkillBranches.NAVIGATION);
                break;

            case MenuSkillTree.INDEX_RESILIENCE:
                consumer.accept(SkillBranches.RESILIENCE);
                break;

            case MenuSkillTree.INDEX_HERBALIST:
                consumer.accept(SkillBranches.HERBALIST);
                break;

            case MenuSkillTree.INDEX_KITTYPET:
                consumer.accept(SkillBranches.KITTYPET);
                break;

            case MenuSkillTree.INDEX_LONER:
                consumer.accept(SkillBranches.LONER);
                break;

            case MenuSkillTree.INDEX_ROGUE:
                consumer.accept(SkillBranches.ROGUE);
                break;

            case MenuSkillTree.INDEX_CITY_CAT:
                consumer.accept(SkillBranches.CITY_CAT);
                break;

            case MenuSkillTree.INDEX_BREEZE_CLAN:
                consumer.accept(SkillBranches.BREEZE_CLAN);
                break;

            case MenuSkillTree.INDEX_ECHO_CLAN:
                consumer.accept(SkillBranches.ECHO_CLAN);
                break;

            case MenuSkillTree.INDEX_CREEK_CLAN:
                consumer.accept(SkillBranches.CREEK_CLAN);
                break;

            case MenuSkillTree.INDEX_SHADE_CLAN:
                consumer.accept(SkillBranches.SHADE_CLAN);
                break;

            case MenuSkillTree.INDEX_BACK:
                player.closeInventory();
                break;
        }
    }

    private void handlePerksMenuClick(int index, Player player) {
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Runnable back = () -> {
                MenuSkillTree.open(player);
                MENUS_OPENED.remove(player.getUniqueId());
            };
            BiConsumer<SkillBranches, Double> consumer = (branch, balance) -> {
                PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                Skills skill = MenuSkillTreePath.getSkillByIndex(index, MENUS_OPENED.get(player.getUniqueId()).getBranch());
                SkillEntity skillEntity = entity.getAbility(skill);
                if (skillEntity == null) {
                    skillEntity = new SkillEntity();
                    skillEntity.setSkill(skill);
                    entity.getAbilityBranch(skill).getSkills().add(skillEntity);
                }
                if (skill.isActive() && skillEntity.getProgress() > 0) {
                    player.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_ALREADY_UNLOCKED);
                    return;
                }
                if (!skill.isActive() && skillEntity.getProgress() >= SkillBranches.UNLOCK_SKILL_TIER * skill.getMaxTiers()) {
                    player.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_ALREADY_UNLOCKED);
                    return;
                }
                if (entity.getXpPerks() < balance) {
                    player.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_NOT_ENOUGH_POINTS);
                    return;
                }
                session.beginTransaction();
                entity.setXpPerks(entity.getXpPerks() - balance);
                skillEntity.setProgress(skillEntity.getProgress() + balance);
                session.persist(entity);
                session.getTransaction().commit();
                EventsCore.PLAYERS_CACHE.put(player.getUniqueId(), entity);
                if (skill == Skills.IRON_HIDE) {
                    SkillsUtils.updateIronHideArmor(player, entity.getAbilityTier(Skills.IRON_HIDE));
                }
                if (skill == Skills.HARD_KNOCK_LIFE) {
                    SkillsUtils.updateHardKnockLifeArmor(player);
                }
                MENUS_STORED.get(player.getUniqueId()).removeIf(menu -> menu.getBranch() == branch);
                MENUS_OPENED.get(player.getUniqueId()).open(player);
                MENUS_STORED.get(player.getUniqueId()).add(MENUS_OPENED.get(player.getUniqueId()));
                if (skill.isActive()) {
                    PlayersUtils.synchronizeInventory(player, entity);
                }
            };
            switch (MENUS_OPENED.get(player.getUniqueId()).getBranch()) {
                case SkillBranches.HUNTING:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;

                        case MenuSkillTreePath.INDEX_PREY_SENSE:
                        case MenuSkillTreePath.INDEX_HUNTERS_COMPASS:
                        case MenuSkillTreePath.INDEX_LOW_SWEEP:
                            consumer.accept(SkillBranches.HUNTING, SkillBranches.UNLOCK_SKILL);
                            break;

                        case MenuSkillTreePath.INDEX_SILENT_PAW:
                        case MenuSkillTreePath.INDEX_BLOOD_HUNTER:
                        case MenuSkillTreePath.INDEX_EFFICIENT_KILL:
                            consumer.accept(SkillBranches.HUNTING, SkillBranches.UNLOCK_SKILL_TIER);
                            break;

                    }
                    break;

                case SkillBranches.NAVIGATION:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;

                        case MenuSkillTreePath.INDEX_LOCATION_AWARENESS:
                        case MenuSkillTreePath.INDEX_PATHFINDING_BOOST:
                            consumer.accept(SkillBranches.NAVIGATION, SkillBranches.UNLOCK_SKILL);
                            break;

                        case MenuSkillTreePath.INDEX_TRAIL_MEMORY:
                        case MenuSkillTreePath.INDEX_ENDURANCE_TRAVELER:
                        case MenuSkillTreePath.INDEX_CLIMBERS_GRACE:
                            consumer.accept(SkillBranches.NAVIGATION, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.RESILIENCE:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;

                        case MenuSkillTreePath.INDEX_HOLD_ON:
                        case MenuSkillTreePath.INDEX_ON_YOUR_PAWS:
                            consumer.accept(SkillBranches.RESILIENCE, SkillBranches.UNLOCK_SKILL);
                            break;

                        case MenuSkillTreePath.INDEX_IRON_HIDE:
                        case MenuSkillTreePath.INDEX_IMMUNE_SYSTEM:
                        case MenuSkillTreePath.INDEX_THICK_COAT:
                        case MenuSkillTreePath.INDEX_HEARTY_APPETITE:
                        case MenuSkillTreePath.INDEX_BEAST_OF_BURDEN:
                            consumer.accept(SkillBranches.RESILIENCE, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.HERBALIST:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;

                        case MenuSkillTreePath.INDEX_HERB_KNOWLEDGE:
                        case MenuSkillTreePath.INDEX_BREW_REMEDY:
                            consumer.accept(SkillBranches.HERBALIST, SkillBranches.UNLOCK_SKILL);
                            break;

                        case MenuSkillTreePath.INDEX_QUICK_GATHERER:
                        case MenuSkillTreePath.INDEX_BOTANICAL_LORE:
                        case MenuSkillTreePath.INDEX_CLEAN_PAWS:
                            consumer.accept(SkillBranches.HERBALIST, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;
                case SkillBranches.KITTYPET:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;
                        case MenuSkillTreePath.INDEX_WELL_FED:
                        case MenuSkillTreePath.INDEX_PAMPERED:
                        case MenuSkillTreePath.INDEX_SHELTERED_MIND:
                            consumer.accept(SkillBranches.KITTYPET, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.LONER:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;
                        case MenuSkillTreePath.INDEX_TRACKER:
                        case MenuSkillTreePath.INDEX_CRAFTY:
                        case MenuSkillTreePath.INDEX_FLEXIBLE_MORALS:
                            consumer.accept(SkillBranches.LONER, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.ROGUE:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;
                        case MenuSkillTreePath.INDEX_AMBUSHER:
                        case MenuSkillTreePath.INDEX_SCAVENGE:
                        case MenuSkillTreePath.INDEX_HARD_KNOCK_LIFE:
                            consumer.accept(SkillBranches.ROGUE, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.CITY_CAT:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;
                        case MenuSkillTreePath.INDEX_URBAN_NAVIGATION:
                        case MenuSkillTreePath.INDEX_RAT_CATCHER:
                        case MenuSkillTreePath.INDEX_DISEASE_RESISTANCE:
                            consumer.accept(SkillBranches.CITY_CAT, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.BREEZE_CLAN:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;
                        case MenuSkillTreePath.INDEX_SPEED_OF_THE_MOOR:
                        case MenuSkillTreePath.INDEX_LIGHTSTEP:
                        case MenuSkillTreePath.INDEX_SHARP_WIND:
                            consumer.accept(SkillBranches.BREEZE_CLAN, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.ECHO_CLAN:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;
                        case MenuSkillTreePath.INDEX_THICK_PELT:
                        case MenuSkillTreePath.INDEX_FOREST_COVER:
                        case MenuSkillTreePath.INDEX_STUNNING_BLOW:
                            consumer.accept(SkillBranches.ECHO_CLAN, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.CREEK_CLAN:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;
                        case MenuSkillTreePath.INDEX_STRONG_SWIMMER:
                        case MenuSkillTreePath.INDEX_AQUA_BALANCE:
                        case MenuSkillTreePath.INDEX_WATERS_RESILIENCE:
                            consumer.accept(SkillBranches.CREEK_CLAN, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;

                case SkillBranches.SHADE_CLAN:
                    switch (index) {
                        case MenuSkillTree.INDEX_BACK:
                            back.run();
                            break;
                        case MenuSkillTreePath.INDEX_NIGHTSTALKER:
                        case MenuSkillTreePath.INDEX_TOXIC_CLAWS:
                        case MenuSkillTreePath.INDEX_SILENT_KILL:
                            consumer.accept(SkillBranches.SHADE_CLAN, SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;
            }
        }
    }
}
