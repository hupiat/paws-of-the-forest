package org.warriorcats.pawsOfTheForest.players;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.clans.ClanEntity;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;

import java.util.UUID;

@Data
@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    // No default initializer here as it is supposed to come from Bukkit getUniqueId()
    private UUID uuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "xp", nullable = false)
    private double xp;

    @Column(name = "coins", nullable = false)
    private long coins;

    @OneToOne
    @JoinColumn(name = "clan_uuid")
    private ClanEntity clan;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settings_uuid", nullable = false)
    private SettingsEntity settings;
}
