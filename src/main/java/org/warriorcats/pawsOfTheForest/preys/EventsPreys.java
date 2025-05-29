package org.warriorcats.pawsOfTheForest.preys;

import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.ModelEngine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.PreysConf;
import org.warriorcats.pawsOfTheForest.core.huds.HUD;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.ModelEngineUtils;

import java.util.Optional;

public class EventsPreys implements Listener {

    // Handling xp and coins giving when killing a prey
    @EventHandler
    public void on(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();

        String entityType = event.getEntityType().name().toUpperCase();

        ModeledEntity modeledEntity = ModelEngine.getModeledEntity(event.getEntity());
        if (modeledEntity != null) {
            entityType = ModelEngineUtils.getModelName(modeledEntity).toUpperCase();
        }

        Optional<Prey> existingPrey = Optional.empty();
        for (Prey prey : PreysConf.Preys.PREYS) {
            if (prey.entityType().equals(entityType)) {
                existingPrey = Optional.of(prey);
                break;
            }
        }

        if (existingPrey.isPresent()) {
            Prey prey = existingPrey.get();
            HibernateUtils.withTransaction(((transaction, session) -> {
                PlayerEntity player = session.get(PlayerEntity.class, killer.getUniqueId());
                player.setXp(player.getXp() + prey.xp());
                player.setCoins(player.getCoins() + prey.coins());
                HUD.updateXpProgressBar(killer, player);
            }));
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.XP_EARNED + prey.xp());
            killer.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.COINS_EARNED + prey.coins());
        }
    }
}
