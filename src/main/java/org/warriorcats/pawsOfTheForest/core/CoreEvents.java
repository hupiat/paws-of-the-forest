package org.warriorcats.pawsOfTheForest.core;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.hibernate.Session;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.utils.HibernateUtils;

public class CoreEvents implements Listener {

    @EventHandler
    public void on(PlayerJoinEvent event) {
        // Saving player data at joining if it does not exist

        try (Session session = HibernateUtils.getSessionFactory().openSession()) {
            session.beginTransaction();

            PlayerEntity existing = session.get(PlayerEntity.class, event.getPlayer().getUniqueId());

            if (existing == null) {
                existing = new PlayerEntity();
                existing.setUuid(event.getPlayer().getUniqueId());
                session.persist(existing);
            }

            session.getTransaction().commit();
        }
    }
}
