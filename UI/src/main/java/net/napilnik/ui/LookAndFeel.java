/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.napilnik.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author malyshev
 */
public class LookAndFeel {

    public static void apply(Class mainClass) {
        System.setProperty("sun.java2d.dpiaware", "true");

        try (InputStream fis = mainClass.getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(fis);
        } catch (IOException ex) {
            Logger.getLogger(mainClass.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(mainClass.getName()).log(Level.SEVERE, null, ex);
        }
        //</editor-fold>
    }
}
