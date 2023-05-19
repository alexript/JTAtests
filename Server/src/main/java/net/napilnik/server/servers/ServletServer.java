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
package net.napilnik.server.servers;

import net.napilnik.server.IServer;
import org.eclipse.jetty.server.Server;

/**
 *
 * @author malyshev
 */
public class ServletServer implements IServer {

    private final Server server;

    public ServletServer(HttpServer httpServer) {
        server = httpServer.getServer();

//        URL resource = ServletServer.class.getResource("/META-INF/servlets/activemq-web-console-5.15.9.war");
//        if (resource != null) {
//            WebAppContext webAppContext = new WebAppContext(resource.toExternalForm(), "/active");
//            server.setHandler(webAppContext);
//        }
    }

    @Override
    public void start() throws Exception {

    }

    @Override
    public void stop() throws Exception {

    }

}
