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
        if (illness.getDaysBeforeWorsened() == 0) {
            return 0;
        }
        long elapsed = new Date().getTime() - gotAt.getTime();
        long threshold = (long) illness.getDaysBeforeWorsened() * 20 * 60 * 1000;
        return elapsed >= threshold ? 2 : 0;
    }

    public boolean isWorsened() {
        return getAmplifier() > 0;
    }
}
