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
import jakarta.persistence.Persistence;
import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author alexript
 */
public class DocumentTest {

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

            Document found = c.find(id);
            assertNotNull(found);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void testCreateChilds() {
        printTestHeader("testCreateChilds");
        Document parentDoc = new Document("parent");
        Document childDoc = new Document("child");

        try (DocumentController c = new DocumentController(emf)) {
            c.create(parentDoc);
            c.create(childDoc);
            childDoc.addParentDocument(parentDoc);
            boolean result = c.update(childDoc);
            assertTrue(result);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void testFindChilds() {
        printTestHeader("testFindChilds");
        Document parentDoc = new Document("parent");
        Document childDoc = new Document("child");

        try (DocumentController c = new DocumentController(emf)) {
            c.create(parentDoc);
            c.create(childDoc);
            childDoc.addParentDocument(parentDoc);
            c.update(childDoc);

            Collection<Document> expected = parentDoc.getChildDocuments();
            List<Document> result = c.getChilds(parentDoc);
            assertIterableEquals(expected, result);
        } catch (Exception ex) {
            fail(ex);
        }
    }

    @Test
    public void testFindChildsInNewEM() {
        printTestHeader("testFindChildsInNewEM");
        Document parentDoc = new Document("parent");
        Document childDoc = new Document("child");
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

    }
}
