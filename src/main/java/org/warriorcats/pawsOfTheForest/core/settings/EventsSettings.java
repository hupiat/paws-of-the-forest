package org.warriorcats.pawsOfTheForest.core.settings;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.warriorcats.pawsOfTheForest.PawsOfTheForest;
import org.warriorcats.pawsOfTheForest.core.chats.ChatChannels;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public class EventsSettings implements Listener {

    // Handling chat settings
    @EventHandler
    public void on(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getView().getTitle().equals(MenuSettings.TITLE)) return;

        event.setCancelled(true);
        int slot = event.getRawSlot();

        if (slot == MenuSettings.INDEX_RP_TOGGLE) {
            Bukkit.getScheduler().runTaskAsynchronously(PawsOfTheForest.getInstance(), () -> {
                HibernateUtils.withTransaction(((transaction, session) -> {
                    PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                    boolean current = entity.getSettings().isShowRoleplay();
                    entity.getSettings().setShowRoleplay(!current);
                    if (!entity.getSettings().isShowRoleplay() && ChatChannels.isRoleplay(entity.getSettings().getToggledChat())) {
                        // Resetting the chat toggled if user disabled RP, and it was RP channel
                        entity.getSettings().setToggledChat(ChatChannels.DEFAULT_TOGGLED);
                    }
                }));
                Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
                    player.openInventory(MenuSettings.create(player));
                });
            });
        }

        if (slot == MenuSettings.INDEX_CHAT_DROPDOWN) {
            Bukkit.getScheduler().runTaskAsynchronously(PawsOfTheForest.getInstance(), () -> {
                HibernateUtils.withTransaction(((transaction, session) -> {
                    PlayerEntity entity = session.get(PlayerEntity.class, player.getUniqueId());
                    ChatChannels current = entity.getSettings().getToggledChat();
                    ChatChannels next = MenuSettings.getNextChat(player, current);
                    entity.getSettings().setToggledChat(next);
                }));
                Bukkit.getScheduler().runTask(PawsOfTheForest.getInstance(), () -> {
                    player.openInventory(MenuSettings.create(player));
                });
            });
        }
    }

    // Handling Noteblock item interaction
    @EventHandler
    public void on(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.NOTE_BLOCK) return;
        if (event.getAction().toString().contains("RIGHT_CLICK")) {
            player.openInventory(MenuSettings.create(player));
            event.setCancelled(true);
        }
    }
}
