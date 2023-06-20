package org.epos.backoffice.api.controller;

import org.epos.eposdatamodel.Person;
import org.epos.eposdatamodel.State;
import org.epos.handler.dbapi.dbapiimplementation.PersonDBAPI;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.Query;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import static org.epos.backoffice.api.controller.EntityManager.getEntityManager;

class PersonControllerTest {

    @BeforeAll
    public static void setup() throws IOException {
        String s = readFileAsString("src/test/resources/data.sql");
        javax.persistence.EntityManager entityManager = getEntityManager();
        entityManager.getTransaction().begin();
        Query nativeQuery = entityManager.createNativeQuery(s);
        nativeQuery.executeUpdate();
        entityManager.getTransaction().commit();
        entityManager.close();

    }



    public static String readFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)))
                .replaceAll("\n", "")
                .replaceAll("timestamptz", "timestamp")
                .replaceAll("BEGIN;", "")
                .replaceAll("END;", "");
    }

    @Test
    public void test(){
        Person p = new Person();
        p.setUid("ciao");
        p.setState(State.PUBLISHED);
        p.setEditorId("ingestor");
        javax.persistence.EntityManager entityManager = getEntityManager();
        PersonDBAPI dbapi = new PersonDBAPI();
        System.out.println(dbapi.getByUid("andrea.orfino@ingv.it", entityManager));
    }
}