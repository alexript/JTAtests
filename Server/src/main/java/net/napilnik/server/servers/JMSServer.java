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

import bitronix.tm.BitronixTransactionManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.server.IServer;
import org.apache.activemq.broker.BrokerService;

/**
 *
 * @author malyshev
 */
public class JMSServer implements IServer {

    private Thread jtaThread;
    private BrokerService broker;

    public JMSServer() {
        try {
            broker = new BrokerService();
            broker.addConnector("tcp://localhost:61616");
            jtaThread = new Thread(() -> {
                try {

                    broker.start();
                } catch (Exception ex) {
                    Logger.getLogger(JMSServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (Exception ex) {
            Logger.getLogger(JMSServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void start() throws Exception {
        jtaThread.start();
//        Thread.sleep(3000);
    }

    @Override
    public void stop() throws Exception {
        broker.stop();
       
    }

}
