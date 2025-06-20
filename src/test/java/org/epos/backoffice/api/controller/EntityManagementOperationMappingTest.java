package org.epos.backoffice.api.controller;

import abstractapis.AbstractAPI;
import commonapis.LinkedEntityAPI;
import metadataapis.EntityNames;
import model.StatusType;
import org.epos.backoffice.api.util.EPOSDataModelManager;
import org.epos.backoffice.api.util.UserManager;
import org.epos.eposdatamodel.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EntityManagementOperationMappingTest extends TestcontainersLifecycle {

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
    public void testCreateAndGet() {

        Operation operation = new Operation();
        operation.setMethod("GET");
        operation.setTemplate("http://template{?test,test1}");
        operation.setReturns(List.of("application/json"));
        operation.setStatus(StatusType.DRAFT);

        LinkedEntity operationLinkedEntity = EPOSDataModelManager.createEposDataModelEntity(operation, user, EntityNames.OPERATION, Operation.class).getEntity();

        operation.setInstanceId(operationLinkedEntity.getInstanceId());
        operation.setMetaId(operationLinkedEntity.getMetaId());
        operation.setUid(operationLinkedEntity.getUid());

        Mapping mapping1 = new Mapping();
        mapping1.setVariable("test1");
        mapping1.setLabel("label1");
        mapping1.setStatus(StatusType.DRAFT);

        LinkedEntity mapping1LinkedEntity = EPOSDataModelManager.createEposDataModelEntity(mapping1, user, EntityNames.MAPPING, Mapping.class).getEntity();

        mapping1.setInstanceId(operationLinkedEntity.getInstanceId());
        mapping1.setMetaId(operationLinkedEntity.getMetaId());
        mapping1.setUid(operationLinkedEntity.getUid());

        operation.setMapping(List.of(mapping1LinkedEntity));

        System.out.println(operation);
        operationLinkedEntity = EPOSDataModelManager.createEposDataModelEntity(operation, user, EntityNames.OPERATION, Operation.class).getEntity();

        assertEquals(1, ((Operation)LinkedEntityAPI.retrieveFromLinkedEntity(operationLinkedEntity)).getMapping().size());

    }

}
