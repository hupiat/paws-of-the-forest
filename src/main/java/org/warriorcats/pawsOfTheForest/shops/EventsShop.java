package org.warriorcats.pawsOfTheForest.shops;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.warriorcats.pawsOfTheForest.core.configurations.MessagesConf;
import org.warriorcats.pawsOfTheForest.core.configurations.ShopsConf;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public class EventsShop implements Listener {

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
                    EventsCore.PLAYER_CACHE.put(player.getUniqueId(), entity);
                    transaction.commit();
                } else {
                    player.sendMessage(ChatColor.RED + MessagesConf.Preys.NOT_ENOUGH_COINS);
                }
            });
        }
    }
}
