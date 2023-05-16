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

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.integration.jetty9.BTMLifeCycle;
import java.io.File;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author alexript
 */
public class Server {

    private static String getDbPath(String dbName) {
        final File dbFolder = new File("databases", dbName);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        return dbFolder.getAbsolutePath();
    }

    public static void main(String[] args) throws SQLException, Exception {
        new Thread(() -> {
            org.hsqldb.server.Server.main(new String[]{
                "--port", "9001",
                "--database.0", getDbPath("masterServer"),
                "--dbname.0", "masterServer",
                "--remote_open", "true"
            });
        }).start();
        new BitronixTransactionManager().shutdown();

        new Thread(() -> {
            try {
                BrokerService broker = new BrokerService();
                broker.addConnector("tcp://localhost:61616");
                broker.start();
            } catch (Exception ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

        Thread.sleep(3000);

        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(8080);
        for (AbstractHandler h : HttpHandler.getHandlers()) {
            server.setHandler(h);
        }
        server.addManaged(new BTMLifeCycle());
        server.start();

        server.join();
    }
}
