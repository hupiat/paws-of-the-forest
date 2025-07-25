package org.warriorcats.pawsOfTheForest.core.events;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.core.chats.commands.CommandToggleChat;
import org.warriorcats.pawsOfTheForest.vitals.HUD;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillBranchEntity;
import org.warriorcats.pawsOfTheForest.skills.SkillBranches;
import org.warriorcats.pawsOfTheForest.skills.Skills;
import org.warriorcats.pawsOfTheForest.utils.FileUtils;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.HttpServerUtils;
import org.warriorcats.pawsOfTheForest.utils.SkillsUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static org.bukkit.potion.PotionEffectType.*;

public class EventsCore implements Listener {

    public static final int FIGHTING_PLAYERS_SCAN_DELAY_S = 10;

    public static final Set<Player> PLAYERS_FIGHTING = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final Set<Player> PLAYERS_LEAVING = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final Map<UUID, PlayerEntity> PLAYERS_CACHE = new ConcurrentHashMap<>();

    public static final Set<PotionEffectType> FEAR_EFFECTS = Set.of(
            BLINDNESS,
            NAUSEA,
            SLOWNESS
    );

    @EventHandler
    public void on(PlayerJoinEvent event) {
        // Saving player data at joining if it does not exist
        HibernateUtils.withSession(session -> {
            PlayerEntity existing = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());

            if (existing == null) {
                session.beginTransaction();
                existing = new PlayerEntity();
                existing.setUuid(event.getPlayer().getUniqueId());
                existing.setName(event.getPlayer().getName());
                existing.setBirthDate(Instant.now());
                existing.setSettings(new SettingsEntity());
                for (SkillBranches branche : SkillBranches.values()) {
                    SkillBranchEntity brancheEntity = new SkillBranchEntity();
                    brancheEntity.setBranch(branche);
                    existing.getSkillBranches().add(brancheEntity);
                }
                session.persist(existing);
                session.getTransaction().commit();
            } else {
                if (existing.hasAbility(Skills.IRON_HIDE)) {
                    SkillsUtils.updateIronHideArmor(event.getPlayer(), existing.getAbilityTier(Skills.IRON_HIDE));
                }
                if (existing.hasAbility(Skills.HARD_KNOCK_LIFE)) {
                    SkillsUtils.updateHardKnockLifeArmor(event.getPlayer());
                }
            }
            PLAYERS_CACHE.put(event.getPlayer().getUniqueId(), existing);
        });

        // Toggling HUD
        HUD.open(event.getPlayer());

        // Toggling resources pack
        event.getPlayer().setResourcePack("http://localhost:" + HttpServerUtils.RESOURCES_PACK_PORT + "/" + FileUtils.RESOURCES_PACK_PATH);

        // Toggling default chat
        CommandToggleChat.setToggledChat(event.getPlayer(), ChatChannels.DEFAULT_TOGGLED);
    }

    // Handling toggled chats redirections
    @EventHandler
    public void on(AsyncChatEvent event) {

        event.setCancelled(true);

        ChatChannels chatToggled = CommandToggleChat.getToggledChat(event.getPlayer());
        String message = PlainTextComponentSerializer.plainText().serialize(event.message());
        Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
            Bukkit.dispatchCommand(event.getPlayer(), chatToggled.name().toLowerCase() + " " + message);
        });
    }

    // Handling custom events

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        Block block = player.getLocation().getBlock();
        if (block.getType() == Material.POWDER_SNOW) {
            Bukkit.getPluginManager().callEvent(new PlayerFreezeEvent(player));
        }
    }


    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        Consumer<Player> consumer = player -> {
            Bukkit.getPluginManager().callEvent(new PlayerInCombatEvent(player));
            new BukkitRunnable() {
                @Override
                public void run() {
                    PLAYERS_FIGHTING.remove(player);
                    Bukkit.getPluginManager().callEvent(new PlayerOutCombatEvent(player));
                }
            }.runTaskLater(PawsOfTheForest.getInstance(), 20 * FIGHTING_PLAYERS_SCAN_DELAY_S);
        };

        if (event.getDamager() instanceof Player damager && !PLAYERS_FIGHTING.contains(damager)) {
            PLAYERS_FIGHTING.add(damager);
            consumer.accept(damager);
        }

        if (event.getEntity() instanceof Player victim && !PLAYERS_FIGHTING.contains(victim)) {
            PLAYERS_FIGHTING.add(victim);
            consumer.accept(victim);
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent event) {
        PLAYERS_CACHE.remove(event.getPlayer().getUniqueId());
        PLAYERS_LEAVING.add(event.getPlayer());

        new BukkitRunnable() {
            @Override
            public void run() {
                PLAYERS_LEAVING.remove(event.getPlayer());
            }
        }.runTaskLater(PawsOfTheForest.getInstance(), 5 * 20);
    }

    @EventHandler
    public void on(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getNewEffect() == null) return;

        if (FEAR_EFFECTS.contains(event.getNewEffect().getType())) {
            Bukkit.getPluginManager().callEvent(new PlayerFearEvent(player));
        }
    }
}
