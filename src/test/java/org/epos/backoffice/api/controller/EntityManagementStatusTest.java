package org.epos.backoffice.api.controller;

import metadataapis.EntityNames;
import model.StatusType;
import org.epos.backoffice.api.util.ApiResponseMessage;
import org.epos.backoffice.api.util.EPOSDataModelManager;
import org.epos.backoffice.api.util.UserManager;
import org.epos.eposdatamodel.Address;
import org.epos.eposdatamodel.LinkedEntity;
import org.epos.eposdatamodel.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EntityManagementStatusTest extends TestcontainersLifecycle {

    static User user = null;
    static Address address = null;

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

        address = new Address();
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


        System.out.println(address);

        assertNotNull(retrievedAddress);
        assertEquals(le.getEntityType(),EntityNames.ADDRESS.name());
    }

    @Test
    @Order(3)
    public void testUpdateStatusAddressToSubmitted() throws InterruptedException {

        address.setStatus(StatusType.SUBMITTED);


        ApiResponseMessage apiResponseMessage = EPOSDataModelManager.updateEposDataModelEntity(address, user, EntityNames.ADDRESS, Address.class);

        LinkedEntity le = apiResponseMessage.getEntity();

        List<Address> addressList = (List<Address>) EPOSDataModelManager.getEPOSDataModelEposDataModelEntity(le.getMetaId(), le.getInstanceId(), user, EntityNames.ADDRESS, Address.class).getListOfEntities();

        System.out.println(addressList.get(0).toString());
        assertNotNull(addressList);
        assertEquals(1,addressList.size());
        assertEquals(StatusType.SUBMITTED,addressList.get(0).getStatus());
    }

    @Test
    @Order(4)
    public void testUpdateStatusAddressToPublished() throws InterruptedException {

        address.setStatus(StatusType.PUBLISHED);


        ApiResponseMessage apiResponseMessage = EPOSDataModelManager.updateEposDataModelEntity(address, user, EntityNames.ADDRESS, Address.class);

        LinkedEntity le = apiResponseMessage.getEntity();

        List<Address> addressList = (List<Address>) EPOSDataModelManager.getEPOSDataModelEposDataModelEntity(le.getMetaId(), le.getInstanceId(), user, EntityNames.ADDRESS, Address.class).getListOfEntities();

        System.out.println(addressList.get(0).toString());
        assertNotNull(addressList);
        assertEquals(1,addressList.size());
        assertEquals(StatusType.PUBLISHED,addressList.get(0).getStatus());
    }
/*
    @Test
    @Order(4)
    public void testUpdateToDraft() {

        System.out.println(address);

        address.setStatus(StatusType.DRAFT);


        ApiResponseMessage apiResponseMessage = EPOSDataModelManager.updateEposDataModelEntity(address, user, EntityNames.ADDRESS, Address.class);

        System.out.println(apiResponseMessage);

        LinkedEntity le = apiResponseMessage.getEntity();

        List<Address> addressList = (List<Address>) EPOSDataModelManager.getEPOSDataModelEposDataModelEntity(le.getMetaId(), null, user, EntityNames.ADDRESS, Address.class).getListOfEntities();

        assertNotNull(addressList);
        assertEquals(2,addressList.size());
    }*/
}
