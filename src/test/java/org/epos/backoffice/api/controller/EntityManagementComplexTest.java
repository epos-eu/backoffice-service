package org.epos.backoffice.api.controller;

import abstractapis.AbstractAPI;
import metadataapis.EntityNames;
import model.RequestStatusType;
import model.RoleType;
import org.epos.backoffice.api.util.AddUserToGroupBean;
import org.epos.backoffice.api.util.EPOSDataModelManager;
import org.epos.backoffice.api.util.GroupManager;
import org.epos.backoffice.api.util.UserManager;
import org.epos.eposdatamodel.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import usermanagementapis.UserGroupManagementAPI;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EntityManagementComplexTest extends TestcontainersLifecycle {

    static User user = null;
    static Group group = null;

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
    public void testCreateGroup() {
        group = new Group(UUID.randomUUID().toString(), "Test Group", "Test Description");
        GroupManager.createGroup(group, user);

        AddUserToGroupBean addUserToGroupBean = new AddUserToGroupBean();
        addUserToGroupBean.setGroupid(group.getId());
        addUserToGroupBean.setUserid(user.getAuthIdentifier());
        addUserToGroupBean.setRole(RoleType.ADMIN.toString());
        addUserToGroupBean.setStatusType(RequestStatusType.ACCEPTED.toString());

        UserManager.addUserToGroup(addUserToGroupBean,user);

        Group retrieveGroup = UserGroupManagementAPI.retrieveGroupById(group.getId());

        System.out.println("retrieveGroup = " + retrieveGroup);

        assertNotNull(retrieveGroup);
        assertEquals(group.getId(), retrieveGroup.getId());
        assertEquals(group.getName(), retrieveGroup.getName());
        assertEquals(group.getDescription(), retrieveGroup.getDescription());
    }

    @Test
    @Order(3)
    public void testCreateAndGet() {

        Group retrieveGroup = UserGroupManagementAPI.retrieveGroupById(group.getId());
        System.out.println("retrieveGroup = " + retrieveGroup);

        Identifier identifier = new Identifier();
        identifier.setInstanceId(UUID.randomUUID().toString());
        identifier.setMetaId(UUID.randomUUID().toString());
        identifier.setUid(UUID.randomUUID().toString());
        identifier.setType("TYPE");
        identifier.setIdentifier("012345678900");
        identifier.setGroups(List.of(group.getId()));

        LinkedEntity identifierLe = EPOSDataModelManager.createEposDataModelEntity(identifier, user, EntityNames.IDENTIFIER, Identifier.class).getEntity();

        WebService webservice = new WebService();
        webservice.setInstanceId(UUID.randomUUID().toString());
        webservice.setMetaId(UUID.randomUUID().toString());
        webservice.setUid(UUID.randomUUID().toString());
        webservice.setDescription("Test description");
        webservice.setName("Test name");
        webservice.setIdentifier(List.of(identifierLe));
        webservice.setGroups(List.of(group.getId()));

        LinkedEntity webserviceLe = EPOSDataModelManager.createEposDataModelEntity(webservice, user, EntityNames.WEBSERVICE, WebService.class).getEntity();

        Identifier retrievedIdentifier = (Identifier) EPOSDataModelManager.getEPOSDataModelEposDataModelEntity(identifierLe.getMetaId(), identifierLe.getInstanceId(), user, EntityNames.IDENTIFIER, Identifier.class).getListOfEntities().get(0);

        WebService retrievedWebservice = (WebService) EPOSDataModelManager.getEPOSDataModelEposDataModelEntity(webserviceLe.getMetaId(), webserviceLe.getInstanceId(), user, EntityNames.WEBSERVICE, WebService.class).getListOfEntities().get(0);

        assertAll(
                () -> assertEquals(identifier.getType(), retrievedIdentifier.getType()),
                () -> assertEquals(identifier.getIdentifier(), retrievedIdentifier.getIdentifier()),
                () -> assertEquals(identifier.getUid(), retrievedIdentifier.getUid()),
                () -> assertEquals(identifier.getInstanceId(), retrievedIdentifier.getInstanceId()),
                () -> assertEquals(identifier.getMetaId(), retrievedIdentifier.getMetaId()),
                () -> assertEquals(webservice.getUid(), retrievedWebservice.getUid()),
                () -> assertEquals(webservice.getInstanceId(), retrievedWebservice.getInstanceId()),
                () -> assertEquals(webservice.getMetaId(), retrievedWebservice.getMetaId()),
                () -> assertEquals(webservice.getIdentifier(), retrievedWebservice.getIdentifier()),
                () -> assertEquals(webservice.getName(), retrievedWebservice.getName()),
                () -> assertEquals(webservice.getDescription(), retrievedWebservice.getDescription())
        );
    }

    @Test
    @Order(4)
    public void testCreateAndGetOperationAndMapping() {

        Mapping mapping = new Mapping();
        mapping.setInstanceId(UUID.randomUUID().toString());
        mapping.setMetaId(UUID.randomUUID().toString());
        mapping.setUid(UUID.randomUUID().toString());
        mapping.setLabel("test");
        mapping.setGroups(List.of(group.getId()));

        LinkedEntity mappingLe = EPOSDataModelManager.createEposDataModelEntity(mapping, user, EntityNames.MAPPING, Mapping.class).getEntity();

        Operation operation = new Operation();
        operation.setInstanceId(UUID.randomUUID().toString());
        operation.setMetaId(UUID.randomUUID().toString());
        operation.setUid(UUID.randomUUID().toString());
        operation.setMapping(List.of(mappingLe));
        operation.setMethod("GET");
        operation.setGroups(List.of(group.getId()));

        LinkedEntity operationLe = EPOSDataModelManager.createEposDataModelEntity(operation, user, EntityNames.OPERATION, Operation.class).getEntity();

        Operation retrievedOperation = (Operation) EPOSDataModelManager.getEPOSDataModelEposDataModelEntity(operationLe.getMetaId(), operationLe.getInstanceId(), user, EntityNames.OPERATION, Operation.class).getListOfEntities().get(0);

        System.out.println(retrievedOperation);
    }

}
