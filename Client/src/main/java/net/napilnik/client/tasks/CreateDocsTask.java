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
package net.napilnik.client.tasks;

import javax.persistence.EntityManagerFactory;
import java.util.Date;
import net.napilnik.client.ApplicationFrame;
import net.napilnik.client.ClientTask;
import net.napilnik.entitymodel.Application;
import net.napilnik.entitymodel.ApplicationController;
import net.napilnik.entitymodel.Document;
import net.napilnik.entitymodel.DocumentController;
import net.napilnik.entitymodel.transactions.TheTransaction;

/**
 *
 * @author alexript
 */
public class CreateDocsTask implements ClientTask {

    @Override
    public void execute(EntityManagerFactory emf, ApplicationFrame frame) {
        try (ApplicationController ac = new ApplicationController(emf); DocumentController dc = new DocumentController(emf)) {
            String appMnemo = "tenbyten-app";
            Application app = ac.find(appMnemo);
            if (app == null) {
                app = new Application(appMnemo);
                ac.create(app);
            }
            TheTransaction tx = dc.createTransaction();
            try {
                for (int i = 0; i < 10; i++) {
                    dc.create(tx, new Document(app, "tenbyten", "tbt-%d".formatted(new Date().getTime())));
                }
               // new javax.naming.InitialContext().lookup("java:comp/UserTransaction");
                tx.commit();
                ac.update(app);
            } catch (Exception ex) {
                tx.rollback();
            }
        }

    }

    @Override
    public String getTitle() {
        return "Create 10 Documents";
    }

    @Override
    public int getWeight() {
        return 10;
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
