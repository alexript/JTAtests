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

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.Configuration;
import bitronix.tm.TransactionManagerServices;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author alexript
 */
public class Client implements AutoCloseable {

    private final BitronixTransactionManager tm;
    private final EntityManagerFactory emf;

    private BitronixTransactionManager connect() {
        long now = new Date().getTime();
        String clientId = "client-%d".formatted(now);
        Configuration conf = TransactionManagerServices.getConfiguration();
        conf.setServerId(clientId);
        File logsFolder = new File("jta-logs", clientId);
        conf.setLogPart1Filename(new File(logsFolder, "part1.btm").getAbsolutePath());
        conf.setLogPart2Filename(new File(logsFolder, "part2.btm").getAbsolutePath());
        conf.setJndiUserTransactionName("java:comp/UserTransaction");
        conf.setResourceConfigurationFilename("resources.properties");
        return TransactionManagerServices.getTransactionManager();
    }

    public Client() {
        tm = connect();
        emf = Persistence.createEntityManagerFactory("model");
    }

    public final BitronixTransactionManager getTM() {
        return tm;
    }

    public final EntityManagerFactory getEMF() {
        return emf;
    }

    @Override
    public void close() throws Exception {
        emf.close();
        tm.shutdown();
    }

    public static void main(String[] args) {
        System.setProperty("sun.java2d.dpiaware", "true");

        try (InputStream fis = Client.class.getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(fis);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* Set the Nimbus look and feel */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>

        AWTThreadTools.onReady(() -> {
            new ApplicationFrame().setVisible(true);
        });

    }

}
