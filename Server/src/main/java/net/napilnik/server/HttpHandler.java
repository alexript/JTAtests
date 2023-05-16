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
package net.napilnik.server;

import com.google.common.reflect.ClassPath;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author malyshev
 */
public interface HttpHandler {

    static List<AbstractHandler> getHandlers() {
        List<HttpHandler> foundHandlers = new ArrayList<>();
        try {

            final ClassLoader loader = Thread.currentThread().getContextClassLoader();

            String packageName = HttpHandler.class.getPackageName();
            String packagePrefix = "%s.".formatted(packageName);

            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses()) {
                if (info.getName().startsWith(packagePrefix)) {
                    final Class<?> c = info.load();
                    if (!c.isInterface() && !c.isLocalClass() && HttpHandler.class.isAssignableFrom(c)) {
                        try {
                            HttpHandler task = (HttpHandler) c.getDeclaredConstructor().newInstance();
                            foundHandlers.add(task);
                        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<AbstractHandler> handlers = new ArrayList<>();
        for (HttpHandler h : foundHandlers) {
            try {
                Constructor<? extends HttpHandler> constructor = h.getClass().getDeclaredConstructor();
                HttpHandler instance = constructor.newInstance();
                if (instance instanceof AbstractHandler ah) {
                    handlers.add(ah);
                }
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(HttpHandler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return handlers;
    }
}
