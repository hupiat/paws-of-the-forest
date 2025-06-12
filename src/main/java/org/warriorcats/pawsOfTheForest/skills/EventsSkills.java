package org.warriorcats.pawsOfTheForest.skills;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventsSkills implements LoadingListener {

    public static final Map<UUID, MenuSkillTreePath> MENUS_OPENED = new HashMap<>();

    public static final double SILENT_PAW_TIER_PERCENTAGE = 0.1;

    private final Set<UUID> soundPacketsIgnored = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // Handling passive events

    @Override
    public void load() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(
                PawsOfTheForest.getInstance(),
                ListenerPriority.NORMAL,
                PacketType.fromClass(ClientboundSoundPacket.class)
        ) {
            @Override
            public void onPacketSending(PacketEvent event) {
                UUID receiver = event.getPlayer().getUniqueId();
                if (soundPacketsIgnored.remove(receiver)) {
                    return;
                }

                // First, getting the sound name

                StructureModifier<Holder> holderMod =
                        event.getPacket().getSpecificModifier(Holder.class);

                Holder<?> rawHolder = holderMod.read(0);

                String repr = rawHolder.toString();
                int start = repr.indexOf("ResourceKey[");
                int end   = repr.indexOf("]=", start);
                if (start < 0 || end < 0) {
                    return;
                }
                String soundName = repr.substring(start + "ResourceKey[".length(), end);

                String lower = soundName.toLowerCase();
                if (!lower.contains("step")) {
                    return;
                }

                // Then if it is a step, parsing walker and receiver
                // to apply SILENT_PAW logic by cancelling event

                World world = event.getPlayer().getWorld();
                int rawX = event.getPacket().getIntegers().read(0);
                int rawY = event.getPacket().getIntegers().read(1);
                int rawZ = event.getPacket().getIntegers().read(2);
                double x = rawX / 8.0;
                double y = rawY / 8.0;
                double z = rawZ / 8.0;
                Location stepLoc = new Location(world, x, y, z);
                Player walker = null;
                double minDist2 = 1.0;
                for (Player p : world.getPlayers()) {
                    double d2 = p.getLocation().distanceSquared(stepLoc);
                    if (d2 <= minDist2) {
                        walker = p;
                        break;
                    }
                }

                if (walker == null) {
                    return;
                }

                event.setCancelled(true);

                try (Session session = HibernateUtils.getSessionFactory().openSession()) {
                    PlayerEntity pe = session.get(PlayerEntity.class, walker.getUniqueId());
                    int tier = pe.getAbilityTier(Skills.SILENT_PAW);
                    double factor = (tier == 0)
                            ? 1
                            : Math.pow(1.0 - SILENT_PAW_TIER_PERCENTAGE, Math.min(tier, 3));
                    float reducedVol = (float) factor;

                    Location loc = walker.getLocation();
                    double baseRadius = 16.0;
                    for (Player listener : world.getPlayers()) {
                        double effectiveRadius = baseRadius * reducedVol;
                        if (listener.getLocation().distanceSquared(loc) <= effectiveRadius * effectiveRadius) {
                            soundPacketsIgnored.add(listener.getUniqueId());
                            listener.playSound(
                                    loc,
                                    loc.getBlock().getBlockData().getSoundGroup().getStepSound(),
                                    reducedVol,
                                    1.0f
                            );
                        }
                    }
                }
            }
        });
    }

    // Handling HUD clicks

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
                case HUNTING:
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
                case NAVIGATION:
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
                case RESILIENCE:
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
                case HERBALIST:
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
