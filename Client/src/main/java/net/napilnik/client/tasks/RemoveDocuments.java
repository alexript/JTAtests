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
package net.napilnik.client.tasks;

import java.util.Collection;
import java.util.Iterator;
import javax.persistence.EntityManagerFactory;
import net.napilnik.client.ApplicationFrame;
import net.napilnik.client.ClientTask;
import net.napilnik.entitymodel.Application;
import net.napilnik.entitymodel.ApplicationController;
import net.napilnik.entitymodel.Document;

/**
 *
 * @author malyshev
 */
public class RemoveDocuments implements ClientTask {

    @Override
    public void execute(EntityManagerFactory emf, ApplicationFrame frame) {
        try (ApplicationController ac = new ApplicationController(emf)) {
            Application app = ac.find("tenbyten-app");
            if (app == null) {
                return;
            }

            Collection<Document> documents = app.getDocuments();
            int size = documents.size();

            try {
                Iterator<Document> iterator = documents.iterator();
                while (iterator.hasNext()) {
                    Document next = iterator.next();
                    iterator.remove();
                    app.removeDocument(next);
                }
                ac.update(app);
                System.out.println("Delete %d documents.".formatted(size));
            } catch (Exception ex) {

                System.err.println(ex.getMessage());
            }
        }
    }

    @Override
    public String getTitle() {
        return "Delete all documents";
    }

    @Override
    public int getWeight() {
        return 30;
    }

    @Override
    public boolean isConnectionRecuired() {
        return true;
    }

    @Override
    public boolean isDisconnectionRecuired() {
        return false;
    }

}
