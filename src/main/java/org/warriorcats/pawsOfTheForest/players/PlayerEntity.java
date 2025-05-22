package org.warriorcats.pawsOfTheForest.players;

import jakarta.persistence.*;
import lombok.Data;
import org.warriorcats.pawsOfTheForest.clans.ClanEntity;

import java.util.UUID;

@Data
@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @OneToOne
    @JoinColumn(name = "clan_uuid")
    private ClanEntity clan;
}
