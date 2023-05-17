/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.napilnik.ui;

import java.io.PrintStream;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 *
 * @author malyshev
 */
public class UIJulLogger extends StreamHandler {

    private static PrintStream out;

    private PrintStream myout;

    static void setOut(DocumentPrintStream outPrintStream) {
        out = outPrintStream;
    }

    public UIJulLogger() {
        super();
        setOutputStream(System.out);
        myout = null;
    }

    @Override
    public void publish(LogRecord record) {
        if (myout == null && out != null) {
            myout = out;
            setOutputStream(out);
        }
        super.publish(record);
        flush();
    }

    @Override
    public void close() {
        flush();
    }
}
