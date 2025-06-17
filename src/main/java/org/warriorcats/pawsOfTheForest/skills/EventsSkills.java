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
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.events.*;
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
    public static final int BEAST_OF_BURDEN_TIER_VALUE = 9;
    public static final double WELL_FED_TIER_PERCENTAGE = 0.5;

    private final Set<UUID> soundPacketsIgnored = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<UUID, List<FootStep>> footsteps = new ConcurrentHashMap<>();

    @Override
    public void load() {
        // SILENT_PAW
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

        // TRACKER
        new BukkitRunnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                footsteps.values().forEach(list -> list.removeIf(fs -> now - fs.timestamp() >= 5000));

                for (Player tracker : Bukkit.getOnlinePlayers()) {
                    HibernateUtils.withSession(session -> {
                        PlayerEntity entity = session.get(PlayerEntity.class, tracker.getUniqueId());
                        if (!entity.hasAbility(Skills.TRACKER)) {
                            return;
                        }
                        for (FootStep fs : footsteps.values().stream().flatMap(List::stream).toList()) {
                            if (fs.location().getWorld().equals(tracker.getWorld())
                                    && fs.location().distanceSquared(tracker.getLocation()) < 25) {
                                tracker.playEffect(fs.location(), Effect.SHOOT_WHITE_SMOKE, 0);
                            }
                        }
                    });
                }
            }
        }.runTaskTimer(PawsOfTheForest.getInstance(), 0, 10);
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        footsteps.computeIfAbsent(event.getPlayer().getUniqueId(), k -> new ArrayList<>())
                .add(new FootStep(event.getPlayer().getLocation(), System.currentTimeMillis()));
    }

    // EFFICIENT_KILL

    @EventHandler
    public void on(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        Optional<Prey> prey = Prey.fromEntity(event.getEntity());

        if (prey.isEmpty()) {
            return;
        }

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

    // WELL-FED

    @EventHandler
    public void on(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (event.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) return;

        if (player.getFoodLevel() < 20) return;

        HibernateUtils.withSession(session -> {
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!entity.hasAbility(Skills.WELL_FED)) return;

            int tier = entity.getAbilityTier(Skills.WELL_FED);
            double factor = WELL_FED_TIER_PERCENTAGE * tier;

            event.setAmount(event.getAmount() * (1 + factor));
        });
    }

    // SHELTERED_MIND

    @EventHandler
    public void on(PlayerFearEvent event) {
        HibernateUtils.withSession(session -> {
            PlayerEntity entity = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());
            if (!entity.hasAbility(Skills.SHELTERED_MIND)) {
                return;
            }
            EventsCore.FEAR_EFFECTS.forEach(event.getPlayer()::removePotionEffect);
        });
    }
}
