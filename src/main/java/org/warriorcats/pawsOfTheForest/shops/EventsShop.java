package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

import java.util.Optional;

public class EventsShop implements Listener {

    // Handling xp and coins giving
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        Optional<Prey> existingPrey = ShopsConf.Preys.PREYS.stream()
                .filter(prey -> prey.entityType() == event.getEntityType())
                .findFirst();

        if (existingPrey.isPresent()) {
            HibernateUtils.withTransaction(((transaction, session) -> {
                PlayerEntity player = session.get(PlayerEntity.class, killer.getUniqueId());
                player.setXp(player.getXp() + existingPrey.get().xp());
                player.setCoins(player.getCoins() + existingPrey.get().coins());
            }));
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.XP_EARNED + " " + existingPrey.get().xp());
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.COINS_EARNED + " " + existingPrey.get().coins());
        }
    }
}
