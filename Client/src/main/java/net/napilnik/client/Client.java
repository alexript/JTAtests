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
package net.napilnik.client;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.entitymodel.Document;
import net.napilnik.entitymodel.DocumentController;

/**
 *
 * @author alexript
 */
public class Client {

    public static void main(String[] args) {

        BitronixTransactionManager tm = TransactionManagerServices.getTransactionManager();

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("model");
        try (DocumentController c = new DocumentController(emf)) {
            int iterations = 50;
            int counter = 0;
            while (counter < iterations) {

                Document d = new Document("doc.random", Long.toString(new Date().getTime()));
                c.create(d);
                Long id = d.getId();
                Thread.sleep(200);
                long expectExistedId = id / 4;
                Document someDoc = c.find(expectExistedId);
                if (someDoc != null) {
                    someDoc.addChildDocument(d);
                    c.update(someDoc);
                    Thread.sleep(200);
                    List<Document> parents = c.getParents(someDoc);

                }
                counter++;
            }
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        tm.shutdown();
    }
}
