package org.epos.backoffice.bean;

import java.sql.ResultSet;
import java.sql.Statement;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.epos.handler.dbapi.service.DBService;
import org.epos.handler.dbapi.util.HealtCheck;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class LivenessHealthIndicator implements HealthIndicator {

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
			EntityManager em = new DBService().getEntityManager();
			em.createNativeQuery("select * from class_mapping cm").getResultList();
		} catch (Exception ignored){
			return 1;
		}
		return 0;

	}
}
