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
package net.napilnik.client;

import net.napilnik.ui.AWTThreadTools;
import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import static net.napilnik.entitymodel.transactions.TheTransaction.JNDI_TRANSACTION_MANAGER;
import net.napilnik.ui.LookAndFeel;
import org.apache.activemq.command.ActiveMQTopic;

/**
 *
 * @author alexript
 */
public class Client implements AutoCloseable {

    private final BitronixTransactionManager tm;
    private final EntityManagerFactory emf;
    private Configuration conf;

    private BitronixTransactionManager connect() {
        long now = new Date().getTime();
        String clientId = "client-%d".formatted(now);
        conf = TransactionManagerServices.getConfiguration();
        conf.setServerId(clientId);
        File logsFolder = new File("jta-logs", clientId);
        conf.setLogPart1Filename(new File(logsFolder, "part1.btm").getAbsolutePath());
        conf.setLogPart2Filename(new File(logsFolder, "part2.btm").getAbsolutePath());
        conf.setJndiUserTransactionName(JNDI_TRANSACTION_MANAGER);
        conf.setResourceConfigurationFilename("resources.properties");
        return TransactionManagerServices.getTransactionManager();
    }

    public Client() {
        tm = connect();
        try {
            InitialContext context = new InitialContext();
            context.bind("jms/JPACCTopic", new ActiveMQTopic("JPACCTopic"));
        } catch (NamingException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        emf = Persistence.createEntityManagerFactory("model");
    }

    public final BitronixTransactionManager getTM() {
        return tm;
    }

    public final EntityManagerFactory getEMF() {
        return emf;
    }

    public final Configuration getConfiguration() {
        return conf;
    }

    @Override
    public void close() throws Exception {
        emf.close();
        tm.shutdown();
    }

    public static void main(String[] args) {
        LookAndFeel.apply(Client.class);

        AWTThreadTools.onReady(() -> {
            new ApplicationFrame("jtaclient.properties").setVisible(true);
        });

    }

}
