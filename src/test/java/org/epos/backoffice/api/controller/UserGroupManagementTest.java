package org.epos.backoffice.api.controller;

import model.RequestStatusType;
import model.RoleType;
import org.epos.backoffice.api.util.AddUserToGroupBean;
import org.epos.backoffice.api.util.GroupManager;
import org.epos.backoffice.api.util.UserManager;
import org.epos.eposdatamodel.Group;
import org.epos.eposdatamodel.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import usermanagementapis.UserGroupManagementAPI;

import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class UserGroupManagementTest extends TestcontainersLifecycle {

    static User user;
    static Group group;

    @Test
    @Order(1)
    public void testCreateUser() {
        user = new User("testid", "familyname", "givenname", "email@email.email", true);
        UserManager.createUser(user,user);

        User retrieveUser = UserManager.getUser(user.getAuthIdentifier(),user,false).getListOfUsers().get(0);

        assertNotNull(retrieveUser);
        assertEquals(user.getAuthIdentifier(), retrieveUser.getAuthIdentifier());
        assertEquals(user.getLastName(), retrieveUser.getLastName());
        assertEquals(user.getFirstName(), retrieveUser.getFirstName());
        assertEquals(user.getEmail(), retrieveUser.getEmail());
    }

    @Test
    @Order(2)
    public void testUpdateUser() {

        user.setEmail("newemail@email.email");
        user.setLastName("newfamilyname");
        user.setFirstName("newgivenname");

        UserManager.createUser(user,user);

        User retrieveUser = UserManager.getUser(user.getAuthIdentifier(),user,false).getListOfUsers().get(0);

        assertNotNull(retrieveUser);
        assertEquals(user.getAuthIdentifier(), retrieveUser.getAuthIdentifier());
        assertEquals(user.getLastName(), retrieveUser.getLastName());
        assertEquals(user.getFirstName(), retrieveUser.getFirstName());
        assertEquals(user.getEmail(), retrieveUser.getEmail());
    }

    @Test
    @Order(3)
    public void testCreateGroup() {
        group = new Group(UUID.randomUUID().toString(), "Test Group", "Test Decription");
        GroupManager.createGroup(group, user);

        Group retrieveGroup = GroupManager.getGroup(group.getId(), user,false).getListOfGroups().get(0);

        assertNotNull(retrieveGroup);
        assertEquals(group.getId(), retrieveGroup.getId());
        assertEquals(group.getName(), retrieveGroup.getName());
        assertEquals(group.getDescription(), retrieveGroup.getDescription());
    }

    @Test
    @Order(4)
    public void testUpdateGroup() {

        group.setDescription("Test updated description");

        GroupManager.createGroup(group, user);

        Group retrieveGroup = GroupManager.getGroup(group.getId(), user,false).getListOfGroups().get(0);

        assertNotNull(retrieveGroup);
        assertEquals(group.getId(), retrieveGroup.getId());
        assertEquals(group.getName(), retrieveGroup.getName());
        assertEquals(group.getDescription(), retrieveGroup.getDescription());
    }

    @Test
    @Order(5)
    public void testAddUserToGroup() {
        AddUserToGroupBean addUserToGroupBean = new AddUserToGroupBean();
        addUserToGroupBean.setGroupid(group.getId());
        addUserToGroupBean.setUserid(user.getAuthIdentifier());
        addUserToGroupBean.setRole(RoleType.EDITOR.toString());
        addUserToGroupBean.setStatusType(RequestStatusType.PENDING.toString());

        UserManager.addUserToGroup(addUserToGroupBean,user);

        Group retrieveGroup = GroupManager.getGroup(group.getId(), user, false).getListOfGroups().get(0);
        User retrieveUser = UserManager.getUser(user.getAuthIdentifier(),user,false).getListOfUsers().get(0);

        System.out.println(retrieveGroup);
        System.out.println(retrieveUser);

        assertAll(
                () -> assertNotNull(retrieveGroup),
                () -> assertEquals(1, retrieveGroup.getUsers().size()),
                () -> assertEquals(retrieveGroup.getUsers().get(0), retrieveUser.getAuthIdentifier()),
                () -> assertEquals(1, retrieveUser.getGroups().size()),
                () -> assertEquals(retrieveUser.getGroups().get(0).getGroupId(), retrieveGroup.getId()),
                () -> assertEquals(retrieveUser.getGroups().get(0).getRole(), RoleType.EDITOR)
        );
    }

    @Test
    @Order(6)
    public void testAddSameUserToGroup() {
        AddUserToGroupBean addUserToGroupBean = new AddUserToGroupBean();
        addUserToGroupBean.setGroupid(group.getId());
        addUserToGroupBean.setUserid(user.getAuthIdentifier());
        addUserToGroupBean.setRole(RoleType.EDITOR.toString());
        addUserToGroupBean.setStatusType(RequestStatusType.PENDING.toString());

        UserManager.addUserToGroup(addUserToGroupBean,user);

        Group retrieveGroup = GroupManager.getGroup(group.getId(), user, false).getListOfGroups().get(0);
        User retrieveUser = UserManager.getUser(user.getAuthIdentifier(),user,false).getListOfUsers().get(0);

        System.out.println(retrieveGroup);
        System.out.println(retrieveUser);

        assertAll(
                () -> assertNotNull(retrieveGroup),
                () -> assertEquals(1, retrieveGroup.getUsers().size()),
                () -> assertEquals(retrieveGroup.getUsers().get(0), retrieveUser.getAuthIdentifier()),
                () -> assertEquals(1, retrieveUser.getGroups().size()),
                () -> assertEquals(retrieveUser.getGroups().get(0).getGroupId(), retrieveGroup.getId()),
                () -> assertEquals(retrieveUser.getGroups().get(0).getRole(), RoleType.EDITOR)
        );
    }

    @Test
    @Order(7)
    public void testDeleteUser() {
        UserManager.deleteUser(user.getAuthIdentifier(), user);

        assertEquals(new ArrayList<User>(), UserManager.getUser(user.getAuthIdentifier(),user,false).getListOfUsers());

    }

    @Test
    @Order(8)
    public void testDeleteGroup() {

        GroupManager.deleteGroup(group.getId(), user);

        assertEquals(new ArrayList<User>(), GroupManager.getGroup(user.getAuthIdentifier(),user,false).getListOfGroups());
    }


    @Test
    @Order(9)
    public void testCreateGroupWithoutName() {
        Group group = new Group(UUID.randomUUID().toString(), null, "Test Decription");
        GroupManager.createGroup(group, user);

        Group retrieveGroup = GroupManager.getGroup(group.getId(),user,false).getListOfGroups().get(0);

        assertNotNull(retrieveGroup);
        assertEquals(group.getId(), retrieveGroup.getId());
        assertEquals(group.getName(), retrieveGroup.getName());
        assertEquals(group.getDescription(), retrieveGroup.getDescription());
    }
}
