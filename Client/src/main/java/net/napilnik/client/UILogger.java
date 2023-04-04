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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Objects;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

/**
 *
 * based on org.eclipse.persistence.logging.DefaultSessionLog
 */
public class UILogger extends AbstractSessionLog {

    private static LoggerDispatcher dispatcher;

    static void setDispatcher(LoggerDispatcher logDispatcher) {
        dispatcher = logDispatcher;
    }

    public UILogger() {
        super();
    }

    public UILogger(Writer writer) {
        this();
    }

    protected void writeMessage(String message) throws IOException {
        this.getWriter().write(message);
    }

    @Override
    public Writer getWriter() {
        if (printWriter != null) {
            return printWriter;
        }
        return super.getWriter();
    }

    @Override
    public void log(SessionLogEntry entry) {
        if (dispatcher != null) {
            final String nameSpace = entry.getNameSpace();
            final int entryLevel = entry.getLevel();
            if (!shouldLog(entryLevel, nameSpace)) {
                return;
            }

            synchronized (this) {
                dispatch(nameSpace);
                try {
                    printPrefixString(entryLevel, nameSpace);
                    this.getWriter().write(getSupplementDetailString(entry));

                    if (entry.hasMessage()) {
                        writeMessage(formatMessage(entry));
//                        getWriter().write(Helper.cr());
                        getWriter().flush();
                    }

                    if (entry.hasException()) {
                        getWriter().write(Helper.cr());
                        if (shouldLogExceptionStackTrace()) {
                            entry.getException().printStackTrace(new PrintWriter(getWriter()));
                        } else {
                            writeMessage(entry.getException().toString());
                        }
                        getWriter().write(Helper.cr());
                        getWriter().flush();
                    }
                } catch (IOException exception) {
                    throw ValidationException.logIOError(exception);
                }
            }
        }
    }

    private PrintWriter printWriter;
    private String lastNameSpace;

    private void dispatch(String nameSpace) {
        if (printWriter != null && !Objects.equals(lastNameSpace, nameSpace)) {
            printWriter.flush();
        }
        lastNameSpace = nameSpace;
        printWriter = dispatcher.route(nameSpace);
    }
}
