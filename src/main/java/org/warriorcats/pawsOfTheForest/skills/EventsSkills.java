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
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.util.Vector;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.events.LoadingListener;
import org.warriorcats.pawsOfTheForest.core.events.PlayerFreezeEvent;
import org.warriorcats.pawsOfTheForest.core.events.PlayerJumpEvent;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.utils.BiomesUtils;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.MobsUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventsSkills implements LoadingListener {

    public static final double SILENT_PAW_TIER_PERCENTAGE = 0.1;
    public static final double EFFICIENT_KILL_TIER_PERCENTAGE = 0.1;
    public static final double BLOOD_HUNTER_TIER_PERCENTAGE = 0.05;
    public static final double ENDURANCE_TRAVELER_TIER_PERCENTAGE = 0.05;
    public static final double CLIMBERS_GRACE_TIER_PERCENTAGE = 0.1;
    public static final double THICK_COAT_TIER_PERCENTAGE = 0.1;
    public static final double HEARTY_APPETITE_TIER_PERCENTAGE = 0.1;

    private final Set<UUID> soundPacketsIgnored = Collections.newSetFromMap(new ConcurrentHashMap<>());

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

    // ENDURANCE_TRAVELER

    @EventHandler
    public void on(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        if (EventsCore.PLAYERS_FIGHTING.contains(player)) {
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

    // CLIMBERS_GRACE

    @EventHandler
    public void on(PlayerJumpEvent event) {
        Player player = event.getPlayer();
        HibernateUtils.withSession(session -> {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!entity.hasAbility(Skills.CLIMBERS_GRACE)) {
                return;
            }

            int tier = entity.getAbilityTier(Skills.CLIMBERS_GRACE);
            double factor = tier * CLIMBERS_GRACE_TIER_PERCENTAGE;

            player.setVelocity(player.getVelocity().add(new Vector(0d, factor, 0d)));
        });
    }

    // THICK COAT

    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        HibernateUtils.withSession(session -> {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!entity.hasAbility(Skills.THICK_COAT)) {
                return;
            }
            Biome biome = player.getWorld().getBiome(player.getLocation());
            if (BiomesUtils.isHot(biome) && BiomesUtils.isDamageFromFire(event.getDamageSource().getDamageType())) {
                event.setDamage(event.getDamage() + 1);
            }
            if (BiomesUtils.isCold(biome) && BiomesUtils.isDamageFromFreeze(event.getDamageSource().getDamageType())) {
                int tier = entity.getAbilityTier(Skills.THICK_COAT);
                event.setDamage(event.getDamage() * (1 - THICK_COAT_TIER_PERCENTAGE * tier));
            }
        });
    }

    @EventHandler
    public void on(PlayerFreezeEvent event) {
        HibernateUtils.withSession(session -> {
            PlayerEntity entity = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());
            if (!entity.hasAbility(Skills.THICK_COAT)) {
                return;
            }
            event.getPlayer().setFreezeTicks(0);
        });
    }

    // HEARTY_APPETITE

    @EventHandler
    public void on(PlayerItemConsumeEvent event) {
        HibernateUtils.withSession(session -> {
            PlayerEntity entity = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());
            if (!entity.hasAbility(Skills.HEARTY_APPETITE)) {
                return;
            }

            int tier = entity.getAbilityTier(Skills.HEARTY_APPETITE);
            double factor = tier * HEARTY_APPETITE_TIER_PERCENTAGE;

            float currentSaturation = event.getPlayer().getSaturation();

            float bonus = (float) (event.getPlayer().getFoodLevel() * factor);

            event.getPlayer().setSaturation(currentSaturation + bonus);
        });
    }
}
