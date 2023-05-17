/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.napilnik.ui;

import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 *
 * @author malyshev
 */
public class NullLogger extends StreamHandler {

    public NullLogger() {
        super();
    }

    @Override
    public void publish(LogRecord record) {

    }

    @Override
    public void close() {

    }
}
