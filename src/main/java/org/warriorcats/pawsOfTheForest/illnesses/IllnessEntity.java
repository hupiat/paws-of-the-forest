package org.warriorcats.pawsOfTheForest.illnesses;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Entity
@Table(name = "illnesses")
public class IllnessEntity {

    @Id
    @Column(name = "uuid", unique = true, nullable = false)
    private UUID uuid = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "illness", nullable = false)
    private Illnesses illness;

    @Column(name = "got_at", nullable = false)
    private Date gotAt;

    public int getAmplifier() {
        return switch (illness) {
            case UPPER_RESPIRATORY_INFECTION -> {
                long elapsed = new Date().getTime() - gotAt.getTime();
                long threshold = 5L * 20 * 60 * 1000;
                yield elapsed >= threshold ? 2 : 0;
            }
            default -> 0;
        };
    }

    public boolean isWorsened() {
        return getAmplifier() > 0;
    }
}
