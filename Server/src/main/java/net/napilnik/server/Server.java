/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package net.napilnik.server;

import java.io.File;
import java.sql.SQLException;

/**
 *
 * @author alexript
 */
public class Server {

    private static String getDbPath(String dbName) {
        final File dbFolder = new File("databases", dbName);
        if(!dbFolder.exists()) {
            dbFolder.mkdirs();
        }
        return dbFolder.getAbsolutePath();
    }
    
    public static void main(String[] args) throws SQLException {
        org.hsqldb.server.Server.main(new String[]{
            "--port", "9001",
            "--database.0", getDbPath("masterServer"),
            "--dbname.0", "masterServer",
            "--remote_open", "true"
        });
    }
}
