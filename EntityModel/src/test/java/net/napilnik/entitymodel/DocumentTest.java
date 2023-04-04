/*
 * Copyright 2023 malyshev.
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

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Document and DocumentController. SQL query logging for executed SQL
 * requests monitoring.
 *
 * @author alexript
 */
public class DocumentTest {

    private static EntityManagerFactory emf;

    private static final String MNEMO_1 = "doc1";
    private static final String MNEMO_2 = "doc2";
    private static final String MNEMO_3 = "doc3";

    /**
     * Same emf for test scope.
     */
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

//<editor-fold defaultstate="collapsed" desc="Nice test results delimiters.">
    private static Date printTestHeader(String name) {
        Date now = new Date();
        System.out.println("-ts- Test: %1$s -------------- <start: %2$tH:%2$tM:%2$tS> ---".formatted(name, now));
        return now;
    }

    private static void printTestFooter(String name) {
        printTestFooter(name, null);
    }

    private static void printTestFooter(String name, Date startNow) {
        if (startNow == null) {
            System.out.println("-te- Test: %s -------------- <end: %2$tH:%2$tM:%2$tS> ---\n".formatted(name, new Date()));
        } else {
            Date now = new Date();
            System.out.println("-te- Test: %1$s -------------- <end: %2$tH:%2$tM:%2$tS, len: %3$dms> ---\n".formatted(name, now, now.getTime() - startNow.getTime()));
        }
    }
//</editor-fold>

    /**
     * AlphaNumeric codes expected. Allows '-' and '_' symbols.
     */
    @Test
    public void testCodeFormat() {
        printTestHeader("testCodeFormat");
        assertDoesNotThrow(() -> new Document(MNEMO_1, "1"));
        assertDoesNotThrow(() -> new Document(MNEMO_1, "12"));
        assertDoesNotThrow(() -> new Document(MNEMO_1, "12-3"));
        assertDoesNotThrow(() -> new Document(MNEMO_1, "12-3_4"));
        assertDoesNotThrow(() -> new Document(MNEMO_1, "12-3_4a"));
        assertDoesNotThrow(() -> new Document(MNEMO_1, "12-3_4aA"));

        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(MNEMO_1, "12-3 _4aA"));
        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(MNEMO_1, "12-3#4aA"));
        assertThrows(
                IllegalArgumentException.class,
                () -> new Document(MNEMO_1, ""));
        printTestFooter("testCodeFormat");
    }

    /**
     * Check no errors on Document persist.
     */
    @Test
    public void testCreate() {
        Date startNow = printTestHeader("testCreate");
        Document doc = new Document(MNEMO_1, "testCreate");
        try (DocumentController c = new DocumentController(emf)) {
            boolean result = c.create(doc);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testCreate", startNow);
    }

    /**
     * Check no errors on Document remove.
     */
    @Test
    public void testDelete() {
        Date startNow = printTestHeader("testDelete");
        Document doc = new Document(MNEMO_1, "testDelete");
        try (DocumentController c = new DocumentController(emf)) {
            c.create(doc);
            boolean result = c.delete(doc);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testDelete", startNow);
    }

    /**
     * Check no errors on Document merge.
     */
    @Test
    public void testUpdate() {
        Date startNow = printTestHeader("testUpdate");
        Document doc = new Document(MNEMO_1, "testUpdate");
        try (DocumentController c = new DocumentController(emf)) {
            c.create(doc);
            doc.setCode("testUpdate2");
            boolean result = c.update(doc);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testUpdate", startNow);
    }

    /**
     * Check Document find by Primary Key value.
     */
    @Test
    public void testFind() {
        Date startNow = printTestHeader("testFind");
        Document doc = new Document(MNEMO_1, "testFind");
        try (DocumentController c = new DocumentController(emf)) {
            c.create(doc);
            Long id = doc.getId();

            Document found = c.find(id);
            assertNotNull(found);
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testFind", startNow);
    }

    /**
     * Check parent-child linkage with no errors.
     */
    @Test
    public void testCreateChilds() {
        Date startNow = printTestHeader("testCreateChilds");
        Document parentDoc = new Document(MNEMO_1, "parent");
        Document childDoc = new Document(MNEMO_1, "child");

        try (DocumentController c = new DocumentController(emf)) {
            c.create(parentDoc);
            c.create(childDoc);
            childDoc.addParentDocument(parentDoc);
            boolean result = c.update(childDoc);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testCreateChilds", startNow);
    }

    /**
     * Check getChildren query result is the same as collection inside of the
     * Entity.
     */
    @Test
    public void testFindChilds() {
        Date startNow = printTestHeader("testFindChilds");
        Document parentDoc = new Document(MNEMO_1, "parent");
        Document childDoc = new Document(MNEMO_1, "child");

        try (DocumentController c = new DocumentController(emf)) {
            c.create(parentDoc);
            c.create(childDoc);
            childDoc.addParentDocument(parentDoc);
            c.update(childDoc);

            Collection<Document> expected = parentDoc.getChildDocuments();
            List<Document> result = c.getChildren(parentDoc);
            assertIterableEquals(expected, result);
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testFindChilds", startNow);
    }

    /**
     * Check persist and find in different EntityManager instances.
     */
    @Test
    public void testFindChildsInNewEM() {
        Date startNow = printTestHeader("testFindChildsInNewEM");
        Document parentDoc = new Document(MNEMO_1, "parent");
        Document childDoc = new Document(MNEMO_1, "child");
        Collection<Document> expected = null;
        Long parentId = null;
        Long childId = null;

        try (DocumentController c = new DocumentController(emf)) {
            c.create(parentDoc);
            c.create(childDoc);
            childDoc.addParentDocument(parentDoc);
            c.update(childDoc);
            childId = childDoc.getId();
            parentId = parentDoc.getId();
            expected = parentDoc.getChildDocuments();
            assertFalse(expected.isEmpty());
        } catch (Exception ex) {
            fail(ex);
        }

        try (DocumentController c = new DocumentController(emf)) {
            Document foundParent = c.find(parentId);
            Collection<Document> result = foundParent.getChildDocuments();
            assertFalse(result.isEmpty());
            assertIterableEquals(expected, result);
            Document[] childArray = result.toArray(Document[]::new);
            assertEquals(childId, childArray[0].getId());
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testFindChildsInNewEM", startNow);
    }

    /**
     * Test getChildrenWithMnemo query results.
     */
    @Test
    public void testFindChildsWithMnemo() {
        Date startNow = printTestHeader("testFindChildsWithMnemo");
        Document parentDoc = new Document(MNEMO_1, "parent");
        Document[] children = new Document[]{
            new Document(MNEMO_1, "child1"),
            new Document(MNEMO_1, "child2"),
            new Document(MNEMO_1, "child3"),
            new Document(MNEMO_2, "child4"), // <- this one
        };

        try (DocumentController c = new DocumentController(emf)) {
            c.create(parentDoc);
            for (Document child : children) {
                c.create(child);
                child.addParentDocument(parentDoc);
                c.update(child);
            }

            List<Document> result = c.getChildren(parentDoc, MNEMO_2);
            assertTrue(result.size() == 1);
            Document childDoc = result.get(0);
            assertEquals(MNEMO_2, childDoc.getMnemo());
            assertEquals("child4", childDoc.getCode());
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testFindChildsWithMnemo", startNow);
    }

    /**
     * Check select query by mnemo.
     */
    @Test
    public void testGetByMnemo() {
        Date startNow = printTestHeader("testGetByMnemo");
        int documentsNumber = 10;
        try (DocumentController c = new DocumentController(emf)) {
            for (int counter = 0; counter < documentsNumber; counter++) {
                c.create(new Document(MNEMO_1, "doc-1-" + counter));
                c.create(new Document(MNEMO_2, "doc-2-" + counter));
                c.create(new Document(MNEMO_3, "doc-3-" + counter));
            }
            List<Document> result = c.getByMnemo(MNEMO_3);
            assertEquals(documentsNumber, result.size());
        } catch (Exception ex) {
            fail(ex);
        }
        printTestFooter("testFindChildsWithMnemo", startNow);
    }

    /**
     * Check a fiew persist requests in one transaction.
     */
    @Test
    public void testBasicTransaction() {
        Date startNow = printTestHeader("testBasicTransaction");
        int documentsNumber = 10;
        try (DocumentController c = new DocumentController(emf)) {
            EntityTransaction tr = c.createTransaction();
            try {
                for (int counter = 0; counter < documentsNumber; counter++) {
                    c.create(tr, new Document(MNEMO_1, "doc-transacted-" + counter));
                }
                tr.commit();
            } catch (Exception ex) {
                tr.rollback();
                fail(ex);
            }
        } catch (Exception ex) {
            fail(ex);
        }
        assertTrue(true); // there is no formal success state.
        printTestFooter("testBasicTransaction", startNow);
    }

}
