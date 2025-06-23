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
import org.bukkit.block.Block;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.events.*;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.preys.Prey;
import org.warriorcats.pawsOfTheForest.utils.*;

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
    public static final double FLEXIBLE_MORALS_TIER_PERCENTAGE = 0.1;
    public static final double AMBUSHER_TIER_PERCENTAGE = 0.1;
    public static final double URBAN_NAVIGATION_TIER_PERCENTAGE = 0.15;
    public static final double RAT_CATCHER_TIER_RANGE = 25;
    public static final double SPEED_OF_THE_MOOR_TIER_PERCENTAGE = 0.15;
    public static final double LIGHTSTEP_TIER_PERCENTAGE = 0.5;
    public static final double SHARP_WIND_TIER_PERCENTAGE = 0.1;
    public static final double SHARP_WIND_TIER_DURATION_S = 10;
    public static final double THICK_PELT_TIER_PERCENTAGE = 0.1;

    private final Set<UUID> soundPacketsIgnored = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Map<UUID, List<FootStep>> footsteps = new ConcurrentHashMap<>();
    private final Map<UUID, Float> defaultSpeeds = new ConcurrentHashMap<>();
    private final Map<UUID, Double> entitiesBleeding = new ConcurrentHashMap<>();

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

        // SHARP_WIND bleeding
        final int sharpWindScanDurationTicks = 10;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (UUID uuid : entitiesBleeding.keySet()) {
                    LivingEntity entity = (LivingEntity) Bukkit.getEntity(uuid);
                    if (entity.isDead()) {
                        entitiesBleeding.remove(uuid);
                        return;
                    }

                    entity.damage(1);

                    entity.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, entity.getLocation().add(0, 1, 0), 5, 0.3, 0.5, 0.3, 0.02);

                    entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_GENERIC_HURT, 0.3f, 1.2f);

                    entitiesBleeding.put(uuid, entitiesBleeding.get(uuid) - sharpWindScanDurationTicks);

                    if (entitiesBleeding.get(uuid) <= 0) {
                        entitiesBleeding.remove(uuid);
                    }
                }
            }
        }.runTaskTimer(PawsOfTheForest.getInstance(), 0L, sharpWindScanDurationTicks);
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


    @EventHandler
    public void on(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        // THICK COAT
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
                double factor = THICK_COAT_TIER_PERCENTAGE * tier;
                event.setDamage(event.getDamage() * (1 - factor));
            }
        });

        // LIGHTSTEP
        HibernateUtils.withSession(session -> {
            if (event.getDamageSource().getDamageType() != DamageType.FALL) {
                return;
            }
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!entity.hasAbility(Skills.LIGHTSTEP)) {
                return;
            }
            int tier = entity.getAbilityTier(Skills.LIGHTSTEP);
            double factor = tier * LIGHTSTEP_TIER_PERCENTAGE;
            event.setDamage(event.getDamage() * (1 - factor));
        });

        // THICK_PELT
        HibernateUtils.withSession(session -> {
            if (event.getDamageSource().getDamageType() != DamageType.PLAYER_ATTACK &&
                    event.getDamageSource().getDamageType() != DamageType.MOB_ATTACK) {
                return;
            }
            PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!entity.hasAbility(Skills.THICK_PELT)) {
                return;
            }
            int tier = entity.getAbilityTier(Skills.THICK_PELT);
            double factor = tier * THICK_PELT_TIER_PERCENTAGE;
            event.setDamage(event.getDamage() * (1 - factor));
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


    @EventHandler
    public void on(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();

        // FLEXIBLE_MORALS
        HibernateUtils.withSession(session -> {
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!playerEntity.hasAbility(Skills.FLEXIBLE_MORALS)) {
                return;
            }
            int tier = playerEntity.getAbilityTier(Skills.FLEXIBLE_MORALS);
            if (entity instanceof Villager villager) {
                player.openMerchant(villager, true);
                event.setCancelled(true);
                if (Math.random() < FLEXIBLE_MORALS_TIER_PERCENTAGE * tier) {
                    player.getInventory().addItem(MobsUtils.getRandomLootFromStealing());
                    player.sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_STOLE_FROM_NPC);
                }
            }
        });

        // RAT_CATCHER
        HibernateUtils.withSession(session -> {
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!playerEntity.hasAbility(Skills.RAT_CATCHER)) {
                return;
            }
            entity.remove();
            player.getInventory().addItem(MobsUtils.getRandomLootFromRat());
            player.sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_CAUGHT_RAT);
            event.setCancelled(true);
        });
    }


    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;

        // AMBUSHER
        HibernateUtils.withSession(session -> {
            if (!MobsUtils.isStealthFrom(player, entity)) {
                return;
            }
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!playerEntity.hasAbility(Skills.AMBUSHER)) {
                return;
            }
            int tier = playerEntity.getAbilityTier(Skills.AMBUSHER);
            double factor = tier * AMBUSHER_TIER_PERCENTAGE;
            event.setDamage(event.getDamage() * (1 + factor));
        });

        // SHARP_WIND
        HibernateUtils.withSession(session -> {
            if (!BiomesUtils.isOpenSpace(player.getLocation())) {
                return;
            }
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!playerEntity.hasAbility(Skills.SHARP_WIND)) {
                return;
            }
            int tier = playerEntity.getAbilityTier(Skills.SHARP_WIND);
            double factor = tier * SHARP_WIND_TIER_PERCENTAGE;
            if (Math.random() < factor) {
                entitiesBleeding.put(entity.getUniqueId(), SHARP_WIND_TIER_DURATION_S * 20);
                if (entity instanceof Player damaged) {
                    damaged.sendMessage(ChatColor.RED + MessagesConf.Skills.PLAYER_MESSAGE_BLEEDING);
                }
            }
        });
    }

    // SCAVENGE

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();

        if (!ItemsUtils.isTrashBlock(block.getType())) {
            return;
        }

        HibernateUtils.withSession(session -> {
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!playerEntity.hasAbility(Skills.SCAVENGE)) {
                return;
            }
            player.getInventory().addItem(ItemsUtils.getRandomLootFromTrash());
            player.sendMessage(MessagesConf.Skills.COLOR_FEEDBACK + MessagesConf.Skills.PLAYER_MESSAGE_FOUND_TRASH_LOOT);
            block.setType(Material.DIRT);
            player.playSound(block.getLocation(), Sound.BLOCK_COMPOSTER_EMPTY, 1.0f, 1.0f);
            event.setCancelled(true);
        });
    }

    @EventHandler
    public void on(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        footsteps.computeIfAbsent(player.getUniqueId(), k -> new ArrayList<>())
                .add(new FootStep(player.getLocation(), System.currentTimeMillis()));

        // URBAN_NAVIGATION
        HibernateUtils.withSession(session -> {
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!playerEntity.hasAbility(Skills.URBAN_NAVIGATION)) {
                return;
            }
            Material blockBelow = player.getLocation().subtract(0, 1, 0).getBlock().getType();
            defaultSpeeds.putIfAbsent(player.getUniqueId(), player.getWalkSpeed());
            int tier = playerEntity.getAbilityTier(Skills.URBAN_NAVIGATION);
            double factor = tier * URBAN_NAVIGATION_TIER_PERCENTAGE;
            PlayersUtils.increaseMovementSpeed(player,
                    () -> ItemsUtils.isUrbanBlock(blockBelow), factor, defaultSpeeds.get(player.getUniqueId()));
        });

        // SPEED OF THE MOOR
        HibernateUtils.withSession(session -> {
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!playerEntity.hasAbility(Skills.SPEED_OF_THE_MOOR)) {
                return;
            }
            defaultSpeeds.putIfAbsent(player.getUniqueId(), player.getWalkSpeed());
            int tier = playerEntity.getAbilityTier(Skills.SPEED_OF_THE_MOOR);
            double factor = tier * SPEED_OF_THE_MOOR_TIER_PERCENTAGE;
            PlayersUtils.increaseMovementSpeed(player,
                    () -> BiomesUtils.isPlain(player.getLocation().getBlock().getBiome()),
                    factor,
                    defaultSpeeds.get(player.getUniqueId()));
        });

        // RAT_CATCHER tracking
        HibernateUtils.withSession(session -> {
            PlayerEntity playerEntity = session.get(PlayerEntity.class, player.getUniqueId());
            if (!playerEntity.hasAbility(Skills.RAT_CATCHER)) {
                return;
            }

            int tier = playerEntity.getAbilityTier(Skills.RAT_CATCHER);
            double radius = tier * RAT_CATCHER_TIER_RANGE;

            player.getWorld().getNearbyLivingEntities(player.getLocation(), radius, radius, radius)
                    .stream()
                    .filter(entity -> {
                        Optional<Prey> prey = Prey.fromEntity(entity);
                        return prey.filter(MobsUtils::isRat).isPresent();
                    })
                    .forEach(rat -> {
                        player.spawnParticle(
                                Particle.WITCH,
                                rat.getLocation().add(0, 0.3, 0),
                                10,
                                0.2, 0.5, 0.2,
                                0
                        );
                    });
        });
    }
}

