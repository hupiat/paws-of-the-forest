package org.warriorcats.pawsOfTheForest.skills;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class EventsSkills implements Listener {

    public static final Map<UUID, MenuSkillTreePath> OPENED = new HashMap<>();

    @EventHandler
    public void on(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        String displayName = clicked.getItemMeta().getDisplayName();

        if (event.getView().getTitle().equals(MenuSkillTree.TITLE)) {
            event.setCancelled(true);
            handleMainMenuClick(displayName, player);
        }

        MenuSkillTreePath openedMenu = OPENED.get(player.getUniqueId());
        if (openedMenu != null && event.getView().getTitle().equals(openedMenu.getTitle())) {
            event.setCancelled(true);
            handlePerksMenuClick(displayName, player);
        }
    }

    private void handleMainMenuClick(String displayName, Player player) {
        switch (ChatColor.stripColor(displayName)) {
            case "Hunting":
                player.closeInventory();
                OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.HUNTING));
                OPENED.get(player.getUniqueId()).open(player);
                break;

            case "Navigation":
                player.closeInventory();
                OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.NAVIGATION));
                OPENED.get(player.getUniqueId()).open(player);
                break;

            case "Resilience":
                player.closeInventory();
                OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.RESILIENCE));
                OPENED.get(player.getUniqueId()).open(player);
                break;

            case "Herbalist":
                player.closeInventory();
                OPENED.put(player.getUniqueId(), new MenuSkillTreePath(SkillBranches.HERBALIST));
                OPENED.get(player.getUniqueId()).open(player);
                break;

            case "Close":
                player.closeInventory();
                break;
        }
    }

    private void handlePerksMenuClick(String displayName, Player player) {
        String skillName = ChatColor.stripColor(displayName.split(MenuSkillTreePath.COLOR_HIGHLIGHT.toString())[0]);
        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            Consumer<Double> consumer = (balance) -> {
                PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                Skills skill = Skills.from(skillName);
                SkillEntity skillEntity = entity.getAbility(skill);
                if (skillEntity == null) {
                    skillEntity = new SkillEntity();
                    skillEntity.setSkill(skill);
                    skillEntity.setActive(skill.isActive());
                    entity.getAbilityBranch(skill).getSkills().add(skillEntity);
                }
                if (skillEntity.isActive() && skillEntity.getProgress() > 0) {
                    player.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_ALREADY_UNLOCKED);
                    return;
                }
                if (!skillEntity.isActive() && skillEntity.getProgress() >= SkillBranches.UNLOCK_SKILL_TIER * SkillBranches.MAX_TIER) {
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
            };
            switch (skillName) {
                case "Back":
                    MenuSkillTree.open(player);
                    OPENED.remove(player.getUniqueId());
                    break;

                // Actives

                case "Prey Sense":
                case "Hunter’s Compass":
                case "Low Sweep":

                case "Location Awareness":
                case "Pathfinding Boost":

                case "Hold On!":
                case "On Your Paws!":

                case "Herb Knowledge":
                case "Brew Remedy":
                    consumer.accept(SkillBranches.UNLOCK_SKILL);
                    break;

                // Passives

                case "Silent Paw":
                case "Blood Hunter":
                case "Efficient Kill":

                case "Trail Memory":
                case "Endurance Traveler":
                case "Climber’s Grace":

                case "Iron Hide":
                case "Immune System":
                case "Thick Coat":

                case "Quick Gatherer":
                case "Botanical Lore":
                case "Clean Paws":
                    consumer.accept(SkillBranches.UNLOCK_SKILL_TIER);
                    break;
            }
        }
        OPENED.get(player.getUniqueId()).open(player);
    }
}
