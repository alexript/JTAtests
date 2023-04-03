/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package net.napilnik.entitymodel;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

/**
 *
 * @author alexript
 */
public class DocumentTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("model");
    }

    @AfterAll
    public static void tearDownClass() {
        emf.close();
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    private static void printTestHeader(String name) {
        System.out.println("---- " + name + " -----------------");
    }

    @Test
    public void testCreate() {
        printTestHeader("testCreate");
        Document doc = new Document("testCreate");
        try (DocumentController c = new DocumentController(emf)) {
            boolean result = c.create(doc);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void testDelete() {
        printTestHeader("testDelete");
        Document doc = new Document("testDelete");
        try (DocumentController c = new DocumentController(emf)) {
            c.create(doc);
            boolean result = c.delete(doc);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void testUpdate() {
        printTestHeader("testUpdate");
        Document doc = new Document("testUpdate");
        try (DocumentController c = new DocumentController(emf)) {
            c.create(doc);
            doc.setCode("testUpdate2");
            boolean result = c.update(doc);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void testFind() {
        printTestHeader("testFind");
        Document doc = new Document("testFind");
        try (DocumentController c = new DocumentController(emf)) {
            c.create(doc);
            Long id = doc.getId();

            Document found = c.find(Document.class, id);
            assertNotNull(found);
        } catch (Exception ex) {
            fail(ex);
        }
    }

}
