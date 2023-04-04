/*
 * Copyright 2023 alexript.
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

import java.awt.BorderLayout;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.eclipse.persistence.logging.SessionLog;

/**
 *
 * @author alexript
 */
public class LoggerDispatcher {

    private static final List<String> loggerCatagories = Arrays.asList(SessionLog.loggerCatagories);
    private final JTabbedPane tabs;
    private final PrintWriter defaultPrintWriter;
    private final Map<String, PrintWriter> scopedWriters;

    public LoggerDispatcher(JTabbedPane tabs, PrintStream defaultStream) {
        this.tabs = tabs;
        this.defaultPrintWriter = new PrintWriter(defaultStream);
        scopedWriters = new HashMap<>();
    }

    public PrintWriter route(String nameSpace) {
        if (isDefaultPane(nameSpace)) {
            return defaultPrintWriter;
        }
        addPanel(nameSpace);
        return scopedWriters.get(nameSpace);
    }

    public boolean isDefaultPane(String nameSpace) {
        return nameSpace == null || !loggerCatagories.contains(nameSpace);
    }

    private boolean isPanelExist(String nameSpace) {
        int tabCount = tabs.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            String title = tabs.getTitleAt(i);
            if (title.equals(nameSpace)) {
                return true;
            }
        }
        return false;
    }

    private void addPanel(String nameSpace) {
        if (isPanelExist(nameSpace)) {
            return;
        }
        final JPanel newPanel = createLogPanel(nameSpace);
        tabs.addTab(nameSpace, newPanel);
    }

    private JPanel createLogPanel(String nameSpace) {
        JPanel logPanel = new JPanel();
        final JTextPane logTextPane = new JTextPane();
        JScrollPane logScrollPane = new JScrollPane();
        JToolBar logToolBar = new JToolBar();
        JButton resetLogButton = new JButton();

        logPanel.setLayout(new BorderLayout());
        logTextPane.setEditable(false);
        logScrollPane.setViewportView(logTextPane);

        logToolBar.setOrientation(SwingConstants.VERTICAL);
        logToolBar.setRollover(true);

        resetLogButton.setText("<html>&#8855;");
        resetLogButton.setToolTipText("Clear <%s> log".formatted(nameSpace));
        resetLogButton.setFocusable(false);
        resetLogButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetLogButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetLogButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            Document document = logTextPane.getDocument();
            try {
                document.remove(0, document.getLength());
            } catch (BadLocationException ex) {
                Logger.getLogger(LoggerDispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        logToolBar.add(resetLogButton);

        logPanel.add(logToolBar, BorderLayout.WEST);

        logPanel.add(logScrollPane, BorderLayout.CENTER);

        Document document = logTextPane.getDocument();
        DocumentPrintStream outPrintStream = new DocumentPrintStream(logScrollPane.getVerticalScrollBar(), document, System.out);
        scopedWriters.put(nameSpace, new PrintWriter(outPrintStream));
        return logPanel;
    }
}
