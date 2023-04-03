/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package net.napilnik.server;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.integration.jetty9.BTMLifeCycle;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.activemq.broker.BrokerService;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 *
 * @author alexript
 */
public class Server {

    static class HelloWorld extends AbstractHandler {

        public void handle(String target,
                Request baseRequest,
                HttpServletRequest request,
                HttpServletResponse response)
                throws IOException, ServletException {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println("<h1>Hello World</h1>");
        }
    }

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
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");

        new Thread(() -> {
            try {
                broker.start();
            } catch (Exception ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

        Thread.sleep(3000);

        org.eclipse.jetty.server.Server server = new org.eclipse.jetty.server.Server(8080);
        server.setHandler(new HelloWorld());
        server.addManaged(new BTMLifeCycle());
        server.start();

        server.join();
    }
}
