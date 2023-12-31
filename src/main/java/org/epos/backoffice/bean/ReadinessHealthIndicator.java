package org.epos.backoffice.bean;

import org.epos.handler.dbapi.dbapiimplementation.PersonDBAPI;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class ReadinessHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down().withDetail("No Database Connection", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {
        try {
            PersonDBAPI p = new PersonDBAPI();
            p.getAll();
        } catch (Exception ignored){
            return 1;
        }
        return 0;
    }
}
