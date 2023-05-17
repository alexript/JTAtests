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

import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.JScrollBar;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author malyshev
 */
class DocumentPrintStream extends PrintStream {

    private final Document document;

    private static final Object sync = new Object();
    private final JScrollBar vScroll;
    private boolean passToDelegate;

    public DocumentPrintStream(JScrollBar vScroll, Document document, OutputStream delegateStream) {
        this(vScroll, document, delegateStream, true);
    }

    public DocumentPrintStream(JScrollBar vScroll, Document document, OutputStream delegateStream, boolean passToDelegate) {
        super(delegateStream);
        this.document = document;
        this.vScroll = vScroll;
        this.passToDelegate = passToDelegate;
    }

    public void setConsoleOutput(boolean enable) {
        passToDelegate = enable;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        synchronized (sync) {

            byte[] b = new byte[len];
            for (int i = 0; i < len; i++) {
                b[i] = buf[off + i];
            }

            String string = new String(b);
            int offset = document.getLength();
            try {
                document.insertString(offset, string + "\n", null);
            } catch (BadLocationException e) {
            }
            if (passToDelegate) {
                super.write(buf, off, len);
            }
            AWTThreadTools.onReady(()
                    -> vScroll.setValue(vScroll.getMaximum()));
        }

    }

}
