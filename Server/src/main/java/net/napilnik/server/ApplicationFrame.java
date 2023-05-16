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

import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.server.servers.DatabaseServer;
import net.napilnik.server.servers.HttpServer;
import net.napilnik.server.servers.TransactionsServer;
import net.napilnik.ui.AbstractApplicationFrame;

/**
 *
 * @author malyshev
 */
public class ApplicationFrame extends AbstractApplicationFrame {

    private static final long serialVersionUID = -7600713126343668675L;
    private HttpServer httpServer;
    private DatabaseServer databaseServer;
    private TransactionsServer transactionsServer;

    public ApplicationFrame(String propsFile) {
        super(propsFile);
    }

    @Override
    protected void onWindowShow() {
        try {
            this.databaseServer = new DatabaseServer();
            databaseServer.start();
            transactionsServer = new TransactionsServer();
            transactionsServer.start();
            this.httpServer = new HttpServer();
            httpServer.start();
        } catch (Exception ex) {
            Logger.getLogger(ApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void onInitComponents() {

    }

    @Override
    protected void onWindowClosing() {
        try {
            transactionsServer.stop();
            databaseServer.stop();
            httpServer.stop();
        } catch (Exception ex) {
            Logger.getLogger(ApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
