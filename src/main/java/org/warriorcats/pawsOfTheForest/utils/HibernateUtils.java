package org.warriorcats.pawsOfTheForest.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.warriorcats.pawsOfTheForest.core.events.EventsCore;
import org.warriorcats.pawsOfTheForest.core.settings.SettingsEntity;
import org.warriorcats.pawsOfTheForest.illnesses.IllnessEntity;
import org.warriorcats.pawsOfTheForest.players.PlayerEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillBranchEntity;
import org.warriorcats.pawsOfTheForest.skills.entities.SkillEntity;

import java.util.function.BiFunction;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class HibernateUtils {

    public static void withSession(Consumer<Session> callback) {
        try (Session session = getSessionFactory().openSession()) {
            callback.accept(session);
        }
    }

    public static <T> void withTransaction(BiFunction<Transaction, Session, T> callback) {
        withSession(session -> {
            var transaction = session.beginTransaction();
            T obj = callback.apply(transaction, session);
            if (obj instanceof PlayerEntity player) {
                EventsCore.PLAYERS_CACHE.put(player.getUuid(), player);
            }
            transaction.commit();
        });
    }

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySetting("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver")
                    .applySetting("hibernate.connection.url", "jdbc:mysql://localhost:3306/pawsoftheforest_dev?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true")
                    .applySetting("hibernate.connection.username", "root")
                    .applySetting("hibernate.connection.password", "mysql")
                    .applySetting("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
                    .applySetting("hibernate.hbm2ddl.auto", "create-drop")
                    .applySetting("hibernate.show_sql", "false")
                    .applySetting("hibernate.format_sql", "false")
                    .build();

            MetadataSources sources = new MetadataSources(registry);

            sources.addAnnotatedClass(PlayerEntity.class);
            sources.addAnnotatedClass(SettingsEntity.class);
            sources.addAnnotatedClass(SkillBranchEntity.class);
            sources.addAnnotatedClass(SkillEntity.class);
            sources.addAnnotatedClass(IllnessEntity.class);

            Metadata metadata = sources.getMetadataBuilder().build();
            return metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        sessionFactory.close();
    }

}
