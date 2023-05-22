/*
 * Copyright 2023 alexript.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.napilnik.entitymodel;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Collection;
import java.util.Date;
import static net.napilnik.entitymodel.Headers.printTestFooter;
import static net.napilnik.entitymodel.Headers.printTestHeader;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import net.napilnik.entitymodel.transactions.TheTransaction;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 *
 * @author alexript
 */
public class ApplicationTest {

    private static EntityManagerFactory emf;

    @BeforeAll
    public static void setUpClass() {
        emf = Persistence.createEntityManagerFactory("model-unit-tests");
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

    /**
     * Check no errors on Application persist.
     */
    @Test
    public void testCreate() {
        Date startNow = printTestHeader("ApplicationTest::testCreate");
        Application app = new Application("testCreate");
        try (ApplicationController c = new ApplicationController(emf)) {
            boolean result = c.create(app);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("ApplicationTest::testCreate", startNow);
    }

    /**
     * Check no errors on Application's documents search.
     */
    @Test
    public void testGetDocuments() {
        Date startNow = printTestHeader("ApplicationTest::testGetDocuments");
        final String appMnemo = "app1";
        Application app = new Application(appMnemo);
        try (ApplicationController ac = new ApplicationController(emf)) {
            boolean result = ac.create(app);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }

        try (ApplicationController ac = new ApplicationController(emf); DocumentController dc = new DocumentController(emf)) {

            TheTransaction tx = dc.createTransaction();
            try {
                tx.create(new Document(app, "t", "1"));
                tx.create(new Document(app, "t", "2"));
                tx.create(new Document(app, "t", "3"));
                tx.commit();
                ac.update(app);
            } catch (Exception ex) {
                tx.rollback();
                fail(ex);
            }
        }

        try (ApplicationController ac = new ApplicationController(emf)) {
            Application foundApp = ac.find(appMnemo);
            Collection<Document> documents = foundApp.getDocuments();
            Assertions.assertEquals(3, documents.size());
        }

        printTestFooter("ApplicationTest::testGetDocuments", startNow);
    }

    /**
     * Receive application structure with named query and directly from class
     * field.
     */
    @Test
    public void testGetStructure() {
        Date startNow = printTestHeader("ApplicationTest::testGetStructure");
        String struct = """
                        {root: 'root'}
                        """;
        final String appMnemo = "structuredApp";
        Application app = new Application(appMnemo);
        app.setStructure(struct);
        try (ApplicationController ac = new ApplicationController(emf)) {
            ac.create(app);
            String result = ac.getStructure(appMnemo);
            String expected = struct.trim();
            assertEquals(expected, result);
        }

        try (ApplicationController ac = new ApplicationController(emf)) {
            Application a = ac.find(appMnemo);
            String result = a.getStructure();
            String expected = struct.trim();
            assertEquals(expected, result);
        }
        printTestFooter("ApplicationTest::testGetStructure", startNow);
    }

    @Test
    public void deleteAppDocument() {
        Date startNow = printTestHeader("ApplicationTest::deleteAppDocument");
        final String appMnemo = "delApp";

        // create application
        Application app = new Application(appMnemo);
        try (ApplicationController ac = new ApplicationController(emf)) {
            ac.create(app);
        }

        Document docToRemove = null;
        // creapte application's documents
        try (ApplicationController ac = new ApplicationController(emf); DocumentController dc = new DocumentController(emf)) {

            TheTransaction tx = dc.createTransaction();
            try {
                tx.create(new Document(app, "t", "1"));
                docToRemove = new Document(app, "t", "2");
                tx.create(docToRemove);
                tx.create(new Document(app, "t", "3"));
                tx.commit();
                ac.update(app);
            } catch (Exception ex) {
                tx.rollback();
                fail(ex);
            }
        }

        assertNotNull(docToRemove);

        try (ApplicationController ac = new ApplicationController(emf)) {

            try {
                app.removeDocument(docToRemove);
                ac.update(app);
            } catch (Exception ex) {
                fail(ex);
            }
        }

        // test
        try (ApplicationController ac = new ApplicationController(emf)) {
            Application foundApp = ac.find(appMnemo);
            Collection<Document> documents = foundApp.getDocuments();
            Assertions.assertEquals(2, documents.size());
        }

        printTestFooter("ApplicationTest::deleteAppDocument", startNow);
    }
}
