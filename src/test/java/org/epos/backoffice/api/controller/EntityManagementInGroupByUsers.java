package org.epos.backoffice.api.controller;

import metadataapis.EntityNames;
import org.epos.backoffice.api.util.*;
import org.epos.eposdatamodel.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class EntityManagementInGroupByUsers extends TestcontainersLifecycle {

    static User adminUser = null;
    static User notAdminUser = null;

    @Test
    @Order(1)
    public void testCreateAdminUser() {
        adminUser = new User("testid", "familyname", "givenname", "email@email.email", true);
        UserManager.createUser(adminUser, adminUser);

        User retrieveUser = UserManager.getUser(adminUser.getAuthIdentifier(), adminUser,false).getListOfUsers().get(0);

        assertNotNull(retrieveUser);
        assertEquals(adminUser.getAuthIdentifier(), retrieveUser.getAuthIdentifier());
        assertEquals(adminUser.getLastName(), retrieveUser.getLastName());
        assertEquals(adminUser.getFirstName(), retrieveUser.getFirstName());
        assertEquals(adminUser.getEmail(), retrieveUser.getEmail());
    }

    @Test
    @Order(2)
    public void testCreateNonAdminUserByAdmin() {
        notAdminUser = new User("testid", "familyname", "givenname", "email@email.email", false);
        UserManager.createUser(notAdminUser, adminUser);

        User retrieveUser = UserManager.getUser(notAdminUser.getAuthIdentifier(), adminUser,false).getListOfUsers().get(0);

        assertNotNull(retrieveUser);
        assertEquals(notAdminUser.getAuthIdentifier(), retrieveUser.getAuthIdentifier());
        assertEquals(notAdminUser.getLastName(), retrieveUser.getLastName());
        assertEquals(notAdminUser.getFirstName(), retrieveUser.getFirstName());
        assertEquals(notAdminUser.getEmail(), retrieveUser.getEmail());
    }

    @Test
    @Order(3)
    public void testErrorUserByNonAdminUser() {
        User errorUser = new User("testid", "familyname", "givenname", "email@email.email", false);
        ApiResponseMessage apiResponseMessage = UserManager.createUser(errorUser, notAdminUser);

        assertEquals(apiResponseMessage.getCode(), 1);
    }

    @Test
    @Order(4)
    public void testAddEntityToGroupByAdminUser() {

        Identifier identifier = new Identifier();
        identifier.setInstanceId(UUID.randomUUID().toString());
        identifier.setMetaId(UUID.randomUUID().toString());
        identifier.setUid(UUID.randomUUID().toString());
        identifier.setType("TYPE");
        identifier.setIdentifier("012345678900");

        LinkedEntity identifierLe = EPOSDataModelManager.createEposDataModelEntity(identifier, adminUser, EntityNames.IDENTIFIER, Identifier.class).getEntity();

        Identifier retrievedIdentifier = (Identifier) EPOSDataModelManager.getEPOSDataModelEposDataModelEntity(identifierLe.getMetaId(), identifierLe.getInstanceId(), adminUser, EntityNames.IDENTIFIER, Identifier.class).getListOfEntities().get(0);

        Group metadataGroup = new Group();
        metadataGroup.setId("test");
        metadataGroup.setDescription("test");
        metadataGroup.setName("test");
        GroupManager.createGroup(metadataGroup, adminUser);

        AddEntityToGroupBean addEntityToGroupBean = new AddEntityToGroupBean();
        addEntityToGroupBean.setGroupid(metadataGroup.getId());
        addEntityToGroupBean.setMetaid(identifierLe.getMetaId());

        ApiResponseMessage apiResponseMessage = GroupManager.addEntityToGroup(addEntityToGroupBean, adminUser);

        Group returnGroup = GroupManager.getGroup(metadataGroup.getId(), adminUser,false).getListOfGroups()
                        .stream().filter(group -> group.getId().equals(metadataGroup.getId())).collect(Collectors.toList()).get(0);

        assertAll(
                () -> assertEquals(identifier.getType(), retrievedIdentifier.getType()),
                () -> assertEquals(identifier.getIdentifier(), retrievedIdentifier.getIdentifier()),
                () -> assertEquals(identifier.getUid(), retrievedIdentifier.getUid()),
                () -> assertEquals(identifier.getInstanceId(), retrievedIdentifier.getInstanceId()),
                () -> assertEquals(identifier.getMetaId(), retrievedIdentifier.getMetaId()),
                () -> assertEquals(apiResponseMessage.getCode(), 4),
                () -> assertEquals(returnGroup.getEntities().size(), 1)
        );
    }

}
