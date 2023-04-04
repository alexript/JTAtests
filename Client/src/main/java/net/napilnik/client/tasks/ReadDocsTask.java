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

import jakarta.persistence.EntityManagerFactory;
import net.napilnik.client.ClientTask;
import net.napilnik.entitymodel.Application;
import net.napilnik.entitymodel.ApplicationController;

/**
 *
 * @author alexript
 */
public class ReadDocsTask implements ClientTask{

    @Override
    public void execute(EntityManagerFactory emf) {
        try(ApplicationController ac = new ApplicationController(emf)) {
            Application app = ac.find("tenbyten-app");
            if(app==null) {
                return;
            }
            
            System.out.println("Found %d documents.".formatted(app.getDocuments().size()));
        }
    }

    @Override
    public String getTitle() {
        return "ReRead documents";
    }

    @Override
    public int getWeight() {
        return 20;
    }
    
}
