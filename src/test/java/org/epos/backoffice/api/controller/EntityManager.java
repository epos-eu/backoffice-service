package org.epos.backoffice.api.controller;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.epos.handler.dbapi.service.EntityManagerFactoryProvider;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.HashMap;

public class EntityManager {

    private static EntityManagerFactory instance;

    public static javax.persistence.EntityManager getEntityManager() {
        return getInstance().createEntityManager();
    }


    public static synchronized EntityManagerFactory getInstance() {
        if (instance == null) {

            String persistenceName = "EPOSDataModel";


            HikariConfig hikariConfig = new HikariConfig();
            HashMap<String, Object> properties = new HashMap<>();

            hikariConfig.setDriverClassName("org.h2.Driver");

            String connectionString = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";

            hikariConfig.setJdbcUrl(connectionString);



            DataSource hikariDataSource = new HikariDataSource(hikariConfig);

            properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, hikariDataSource);
            instance = Persistence.createEntityManagerFactory(persistenceName, properties);

        }
        return instance;
    }
}
