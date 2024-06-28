package org.epos.backoffice.api.controller;

import org.epos.backoffice.api.util.UserManager;
import org.epos.eposdatamodel.User;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import usermanagementapis.UserGroupManagementAPI;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class UserManagementTest extends TestcontainersLifecycle {

    @Test
    @Order(1)
    public void testCreateUser() {
        User user = new User("testid", "familyname", "givenname", "email@email.email", true);
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
    public void testUpdateUser() {
        User user = new User("testid", "familyname", "givenname", "email@email.email", true);
        UserManager.createUser(user, user);

        user.setEmail("newemail@email.email");
        user.setLastName("newfamilyname");
        user.setFirstName("newgivenname");

        UserManager.updateUser(user, user);

        User retrieveUser = UserManager.getUser(user.getAuthIdentifier(),user,false).getListOfUsers().get(0);

        assertNotNull(retrieveUser);
        assertEquals(user.getAuthIdentifier(), retrieveUser.getAuthIdentifier());
        assertEquals(user.getLastName(), retrieveUser.getLastName());
        assertEquals(user.getFirstName(), retrieveUser.getFirstName());
        assertEquals(user.getEmail(), retrieveUser.getEmail());
    }

    @Test
    @Order(3)
    public void testDeleteUser() {
        User user = new User("testid", "familyname", "givenname", "email@email.email", true);
        UserManager.createUser(user, user);

        UserManager.deleteUser(user.getAuthIdentifier(), user);

        assertEquals(new ArrayList<User>(),UserManager.getUser(user.getAuthIdentifier(),user,false).getListOfUsers());
    }

}
