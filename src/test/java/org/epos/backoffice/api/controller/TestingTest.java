package org.epos.backoffice.api.controller;

import org.junit.jupiter.api.Test;

import static org.epos.backoffice.api.controller.EntityManager.getEntityManager;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestingTest {
    @Test
    public void testAddition() {
        javax.persistence.EntityManager entityManager = getEntityManager();
        System.out.println(entityManager.isOpen());

        int a = 2;
        int b = 3;
        int result = a + b;
        assertEquals(5, result);
    }
}
