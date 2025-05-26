package org.warriorcats.pawsOfTheForest.shops;

import lombok.Data;
import org.bukkit.entity.EntityType;

@Data
public class Prey {
    private EntityType entityType;
    private double xp;
    private long coins;
}
