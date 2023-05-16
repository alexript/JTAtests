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

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.server.IServer;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.Server;
import org.hsqldb.server.ServerAcl;
import org.hsqldb.server.ServerConfiguration;

/**
 *
 * @author malyshev
 */
public class DatabaseServer implements IServer {

    private static String getDbPath(String dbName) {
        final File dbFolder = new File("databases", dbName);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        return dbFolder.getAbsolutePath();
    }
    private Server server;

    public DatabaseServer() {

        String[] args = new String[]{
            "--port", "9001",
            "--database.0", getDbPath("masterServer"),
            "--dbname.0", "masterServer",
            "--remote_open", "true"
        };

        HsqlProperties argProps = HsqlProperties.argArrayToProps(args, "server");

        String[] errors = argProps.getErrorKeys();

        if (errors.length != 0) {
            Logger.getLogger(DatabaseServer.class.getName()).log(Level.SEVERE, "no value for argument:{0}", errors[0]);
            return;
        }

        ServerConfiguration.translateDefaultDatabaseProperty(argProps);
        ServerConfiguration.translateDefaultNoSystemExitProperty(argProps);
        ServerConfiguration.translateAddressProperty(argProps);

        server = new Server();

        try {
            server.setProperties(argProps);
        } catch (IOException | ServerAcl.AclFormatException e) {
            Logger.getLogger(DatabaseServer.class.getName()).log(Level.SEVERE, "Failed to set properties", e);
        }

    }

    @Override
    public void start() throws Exception {
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.shutdown();
    }

}
