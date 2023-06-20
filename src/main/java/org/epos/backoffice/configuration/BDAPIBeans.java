package org.epos.backoffice.configuration;

import org.epos.handler.dbapi.DBAPIClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BDAPIBeans {
    @Bean
    public DBAPIClient dbapiClient() {
        return new DBAPIClient().metadataMode(false);
    }
}
