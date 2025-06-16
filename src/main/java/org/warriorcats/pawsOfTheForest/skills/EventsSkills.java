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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class EventsSkills implements LoadingListener {

    public static final double SILENT_PAW_TIER_PERCENTAGE = 0.1;
    public static final double EFFICIENT_KILL_TIER_PERCENTAGE = 0.25;
    public static final double BLOOD_HUNTER_TIER_PERCENTAGE = 0.05;
    public static final double ENDURANCE_TRAVELER_TIER_PERCENTAGE = 0.05;

    public static final int FIGHTING_PLAYERS_SCAN_DELAY_S = 10;

    private final Set<Player> PLAYERS_FIGHTING = new HashSet<>();

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

    @EventHandler
    public void on(EntityDeathEvent event) {
        // Checking if a prey has been killed

        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        Optional<Prey> prey = Prey.fromEntity(event.getEntity());

        if (prey.isEmpty()) {
            return;
        }

        // Then checking if the kill is stealth to apply EFFICIENT_KILL

        if (!MobsUtils.isStealthFrom(killer, event.getEntity())) {
            return;
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            PlayerEntity entity = session.get(PlayerEntity.class, killer.getUniqueId());

            if (!entity.hasAbility(Skills.EFFICIENT_KILL)) {
                event.setDroppedExp((int) prey.get().xp());
                return;
            }

            int tier = entity.getAbilityTier(Skills.EFFICIENT_KILL);
            double factor = tier * EFFICIENT_KILL_TIER_PERCENTAGE;
            event.setDroppedExp((int) prey.get().xp() + (int) Math.round(prey.get().xp() * factor));
            event.getDrops().add(MobsUtils.getRandomDropFood(1, (int) Math.round((event.getDrops().size() + tier) * factor)));
        }
    }

    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        Consumer<Player> consumer = player -> new BukkitRunnable() {
            @Override
            public void run() {
                PLAYERS_FIGHTING.remove(player);
            }
        }.runTaskLater(PawsOfTheForest.getInstance(), 20 * FIGHTING_PLAYERS_SCAN_DELAY_S);

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
    public void on(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (PLAYERS_FIGHTING.contains(player)) {
            return;
        }

        int currentLevel = player.getFoodLevel();
        int newLevel = event.getFoodLevel();

        if (newLevel >= currentLevel) {
            return;
        }

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!entity.hasAbility(Skills.ENDURANCE_TRAVELER)) {
                return;
            }

            int tier = entity.getAbilityTier(Skills.ENDURANCE_TRAVELER);

            double factor = tier * ENDURANCE_TRAVELER_TIER_PERCENTAGE;

            int diff = currentLevel - newLevel;
            double reducedDiff = diff * (1.0 - factor);

            event.setFoodLevel(currentLevel - (int) Math.ceil(reducedDiff));
        }
    }
}
