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

import bitronix.tm.integration.jetty9.BTMLifeCycle;
import net.napilnik.server.HttpHandler;
import net.napilnik.server.IServer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author malyshev
 */
public class HttpServer implements IServer {

    private Server server;

    @Override
    public void start() throws Exception {
        this.server = new org.eclipse.jetty.server.Server(8080);
        for (AbstractHandler h : HttpHandler.getHandlers()) {
            server.setHandler(h);
        }
        server.addManaged(new BTMLifeCycle());
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

}
