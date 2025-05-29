package org.warriorcats.pawsOfTheForest.shops;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.entity.BaseEntity;
import com.ticxo.modelengine.api.entity.BukkitEntity;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.core.ModelEngine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.core.huds.HUD;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;
import org.warriorcats.pawsOfTheForest.utils.ModelEngineUtils;

import java.util.Optional;

public class EventsShop implements Listener {

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
        for (Prey prey : ShopsConf.Preys.PREYS) {
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

    // Handling shop HUD management
    @EventHandler
    public void on(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getView().getTitle().equals(MenuShop.TITLE)) {
            event.setCancelled(true);

            int slot = event.getRawSlot();
            ShopItem item = ShopsConf.Shops.SHOP_ITEMS.get(slot);
            if (item == null) return;

            HibernateUtils.withSession(session -> {
                PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());

                long balance = entity.getCoins();

                if (balance >= item.price()) {
                    var transaction = session.beginTransaction();
                    entity.setCoins(balance - item.price());
                    player.getInventory().addItem(item.toItemStack());
                    player.sendMessage(MessagesConf.Preys.COLOR_FEEDBACK + MessagesConf.Preys.MADE_BUY + " " + item.price() + " Paw Coins.");
                    transaction.commit();
                } else {
                    player.sendMessage(ChatColor.RED + MessagesConf.Preys.NOT_ENOUGH_COINS);
                }
            });
        }
    }
}
