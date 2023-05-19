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
