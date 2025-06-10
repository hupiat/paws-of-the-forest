package org.warriorcats.pawsOfTheForest.skills;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "skills")
public class SkillEntity {

    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "skill", nullable = false)
    private Skills skill;

    @Column(name = "progress", nullable = false)
    private double progress;
}
