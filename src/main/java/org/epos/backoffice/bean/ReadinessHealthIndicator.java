package org.epos.backoffice.bean;

import dao.EposDataModelDAO;
import model.User;
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
			EposDataModelDAO dao = new EposDataModelDAO();
			dao.getAllFromDB(User.class);
		} catch (Exception ignored){
			return 1;
		}
		return 0;

	}
}
