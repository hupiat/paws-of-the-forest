package org.warriorcats.pawsOfTheForest.skills.menus;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.SkillEntity;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class EventsSkillsMenu implements Listener {

    public static final Map<UUID, MenuSkillTreePath> MENUS_OPENED = new HashMap<>();

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        int index = event.getSlot();

        if (event.getView().getTitle().equals(MenuSkillTree.TITLE)) {
            event.setCancelled(true);
            handleMainMenuClick(index, player);
        }

        MenuSkillTreePath openedMenu = MENUS_OPENED.get(player.getUniqueId());
        if (openedMenu != null && event.getView().getTitle().equals(openedMenu.getTitle())) {
            event.setCancelled(true);
            handlePerksMenuClick(index, player);
        }
    }

    private void handleMainMenuClick(int index, Player player) {
        switch (index) {
            case MenuSkillTree.INDEX_HUNTING:
                player.closeInventory();
                MENUS_OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.HUNTING));
                MENUS_OPENED.get(player.getUniqueId()).open(player);
                break;

            case MenuSkillTree.INDEX_NAVIGATION:
                player.closeInventory();
                MENUS_OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.NAVIGATION));
                MENUS_OPENED.get(player.getUniqueId()).open(player);
                break;

            case MenuSkillTree.INDEX_RESILIENCE:
                player.closeInventory();
                MENUS_OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.RESILIENCE));
                MENUS_OPENED.get(player.getUniqueId()).open(player);
                break;

            case MenuSkillTree.INDEX_HERBALIST:
                player.closeInventory();
                MENUS_OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.HERBALIST));
                MENUS_OPENED.get(player.getUniqueId()).open(player);
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
            Consumer<Double> consumer = (balance) -> {
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
                MENUS_OPENED.get(player.getUniqueId()).open(player);
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
                            consumer.accept(SkillBranches.UNLOCK_SKILL);
                            break;

                        case MenuSkillTreePath.INDEX_SILENT_PAW:
                        case MenuSkillTreePath.INDEX_BLOOD_HUNTER:
                        case MenuSkillTreePath.INDEX_EFFICIENT_KILL:
                            consumer.accept(SkillBranches.UNLOCK_SKILL_TIER);
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
                            consumer.accept(SkillBranches.UNLOCK_SKILL);
                            break;

                        case MenuSkillTreePath.INDEX_TRAIL_MEMORY:
                        case MenuSkillTreePath.INDEX_ENDURANCE_TRAVELER:
                        case MenuSkillTreePath.INDEX_CLIMBERS_GRACE:
                            consumer.accept(SkillBranches.UNLOCK_SKILL_TIER);
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
                            consumer.accept(SkillBranches.UNLOCK_SKILL);
                            break;

                        case MenuSkillTreePath.INDEX_IRON_HIDE:
                        case MenuSkillTreePath.INDEX_IMMUNE_SYSTEM:
                        case MenuSkillTreePath.INDEX_THICK_COAT:
                        case MenuSkillTreePath.INDEX_HEARTY_APPETITE:
                        case MenuSkillTreePath.INDEX_BEAST_OF_BURDEN:
                            consumer.accept(SkillBranches.UNLOCK_SKILL_TIER);
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
                            consumer.accept(SkillBranches.UNLOCK_SKILL);
                            break;

                        case MenuSkillTreePath.INDEX_QUICK_GATHERER:
                        case MenuSkillTreePath.INDEX_BOTANICAL_LORE:
                        case MenuSkillTreePath.INDEX_CLEAN_PAWS:
                            consumer.accept(SkillBranches.UNLOCK_SKILL_TIER);
                            break;
                    }
                    break;
            }
        }
    }
}
