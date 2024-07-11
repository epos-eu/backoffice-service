package org.epos.backoffice.api.controller;

import abstractapis.AbstractAPI;
import metadataapis.EntityNames;
import org.epos.backoffice.api.util.UserManager;
import org.epos.eposdatamodel.Location;
import org.epos.eposdatamodel.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LocationManagementTest extends TestcontainersLifecycle {

    @Test
    @Order(1)
    public void testCreateLocation() {
        Location location = new Location();
        location.setLocation("POLYGON");

        AbstractAPI api = AbstractAPI.retrieveAPI(EntityNames.LOCATION.name());
        api.create(location, null);
    }

}
