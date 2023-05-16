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
import net.napilnik.server.IServer;

/**
 *
 * @author malyshev
 */
public class DatabaseServer implements IServer {

    private final Thread serverThread;

    private static String getDbPath(String dbName) {
        final File dbFolder = new File("databases", dbName);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        return dbFolder.getAbsolutePath();
    }

    public DatabaseServer() {
        serverThread = new Thread(() -> {
            org.hsqldb.server.Server.main(new String[]{
                "--port", "9001",
                "--database.0", getDbPath("masterServer"),
                "--dbname.0", "masterServer",
                "--remote_open", "true"
            });
        });
    }

    @Override
    public void start() throws Exception {
        serverThread.start();
    }

    @Override
    public void stop() throws Exception {
        serverThread.stop();
    }

}
