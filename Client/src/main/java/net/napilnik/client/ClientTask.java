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
package net.napilnik.client;

import com.google.common.reflect.ClassPath;
import jakarta.persistence.EntityManagerFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexript
 */
public interface ClientTask {

    void execute(EntityManagerFactory emf, ApplicationFrame frame);

    String getTitle();

    int getWeight();

    boolean isConnectionRecuired();

    boolean isDisconnectionRecuired();

    static List<ClientTask> getTasks() {
        List<ClientTask> tasks = new ArrayList<>();
        try {

            final ClassLoader loader = Thread.currentThread().getContextClassLoader();

            String packageName = ClientTask.class.getPackageName();
            String packagePrefix = "%s.".formatted(packageName);

            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(packagePrefix)) {
                    final Class<?> c = info.load();
                    if (!c.isInterface() && !c.isLocalClass() && ClientTask.class.isAssignableFrom(c)) {
                        try {
                            ClientTask task = (ClientTask) c.getDeclaredConstructor().newInstance();
                            tasks.add(task);
                        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            Logger.getLogger(ClientTask.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            tasks.sort((ClientTask o1, ClientTask o2) -> o1.getWeight() - o2.getWeight());

        } catch (IOException ex) {
            Logger.getLogger(ClientTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return tasks;
    }

}
