package org.epos.backoffice.api.controller;

import abstractapis.AbstractAPI;
import metadataapis.EntityNames;
import org.epos.backoffice.api.util.EPOSDataModelManager;
import org.epos.backoffice.api.util.UserManager;
import org.epos.eposdatamodel.Address;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EntityManagementTest extends TestcontainersLifecycle {

    static User user = null;

    @Test
    @Order(1)
    public void testCreateUser() {
        user = new User("testid", "familyname", "givenname", "email@email.email", true);
        UserManager.createUser(user, user);

        User retrieveUser = UserManager.getUser(user.getAuthIdentifier(),user,false).getListOfUsers().get(0);

        assertNotNull(retrieveUser);
        assertEquals(user.getAuthIdentifier(), retrieveUser.getAuthIdentifier());
        assertEquals(user.getLastName(), retrieveUser.getLastName());
        assertEquals(user.getFirstName(), retrieveUser.getFirstName());
        assertEquals(user.getEmail(), retrieveUser.getEmail());
    }

    @Test
    @Order(2)
    public void testCreateAndGetAddress() {

        Address address = new Address();
        address.setInstanceId(UUID.randomUUID().toString());
        address.setMetaId(UUID.randomUUID().toString());
        address.setUid(UUID.randomUUID().toString());
        address.setCountry("Italy");
        address.setCountryCode("IT");
        address.setStreet("Via Roma");
        address.setPostalCode("00100");
        address.setLocality("Rome");

        LinkedEntity le = EPOSDataModelManager.createEposDataModelEntity(address, user, EntityNames.ADDRESS, Address.class).getEntity();

        Address retrievedAddress = (Address) EPOSDataModelManager.getEPOSDataModelEposDataModelEntity(le.getMetaId(), le.getInstanceId(), user, EntityNames.ADDRESS, Address.class).getListOfEntities().get(0);

        LOG.info("RECEIVED:\n"+address.toString());

        assertNotNull(retrievedAddress);
        assertEquals(le.getEntityType(),EntityNames.ADDRESS.name());
    }
/*
    @Test
    @Order(3)
    public void testUpdateAddress() {
        AbstractAPI api = AbstractAPI.retrieveAPI(EntityNames.ADDRESS.name());

        Address address = new Address();
        address.setInstanceId(UUID.randomUUID().toString());
        address.setMetaId(UUID.randomUUID().toString());
        address.setUid(UUID.randomUUID().toString());
        address.setCountry("France");
        address.setCountryCode("FR");
        address.setStreet("Rue de la Paix");
        address.setPostalCode("75002");
        address.setLocality("Paris");

        api.create(address);

        address.setCountry("Spain");
        address.setPostalCode("28001");
        address.setLocality("Madrid");

        api.create(address);

        Address retrievedAddress = (Address) api.retrieve(address.getInstanceId());

        assertNotNull(retrievedAddress);
        assertEquals(address, retrievedAddress);
    }

    @Test
    @Order(4)
    public void testDeleteAddress() {
        AbstractAPI api = AbstractAPI.retrieveAPI(EntityNames.ADDRESS.name());

        Address address = new Address();
        address.setInstanceId(UUID.randomUUID().toString());
        address.setMetaId(UUID.randomUUID().toString());
        address.setUid(UUID.randomUUID().toString());
        address.setCountry("Germany");
        address.setCountryCode("DE");
        address.setStreet("Unter den Linden");
        address.setPostalCode("10117");
        address.setLocality("Berlin");

        api.create(address);

       List<model.Address> addressList = api.getDbaccess().getOneFromDBByInstanceId(address.getInstanceId(),model.Address.class);

        LOG.info(api.getDbaccess().deleteObject(addressList.get(0)).toString());
        Address retrievedAddress = (Address) api.retrieve(address.getInstanceId());

        assertEquals(null,retrievedAddress);
    }
*/

}
