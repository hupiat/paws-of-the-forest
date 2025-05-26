package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.entity.EntityType;

public record Prey(EntityType entityType, double xp, long coins) {
}
