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
package net.napilnik.ui;

import java.awt.Component;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JTabbedPane;
import org.eclipse.persistence.logging.SessionLog;

/**
 *
 * @author alexript
 */
public class LoggerDispatcher {

    private static final List<String> JPA_LOG_CATEGORIES = Arrays.asList(SessionLog.loggerCatagories);

    private static final List<String> loggerCatagories = new ArrayList(JPA_LOG_CATEGORIES);
    private final JTabbedPane tabs;
    private final PrintWriter defaultPrintWriter;
    private final Map<String, PrintWriter> scopedWriters;
    private final AbstractApplicationFrame frame;

    public LoggerDispatcher(AbstractApplicationFrame frame, PrintStream defaultStream) {
        this.frame = frame;
        this.tabs = frame.getLogTabs();
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

    public void registerLogCategory(String title) {
        if (!loggerCatagories.contains(title)) {
            loggerCatagories.add(title);
        }
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
        final LogPanel logPanel = new LogPanel(this, nameSpace, scopedWriters);
        tabs.addTab(nameSpace, logPanel);
    }

    protected void resetPanels() {
        Set<String> namesToRemove = new HashSet<>();
        for (Map.Entry<String, PrintWriter> entry : scopedWriters.entrySet()) {
            if (JPA_LOG_CATEGORIES.contains(entry.getKey())) {
                try (PrintWriter writer = entry.getValue()) {
                    writer.flush();
                }
                namesToRemove.add(entry.getKey());

            }
        }
        for (String name : namesToRemove) {
            scopedWriters.remove(name);
        }

        Component[] components = tabs.getComponents();
        for (Component c : components) {
            if (c instanceof LogPanel logPanel) {
                logPanel.clean();
            }
        }
        int total = tabs.getComponentCount();
        if (total > 1) {
            for (int i = total - 1; i > 0; i--) {
                String title = tabs.getTitleAt(i);
                if (JPA_LOG_CATEGORIES.contains(title)) {
                    tabs.remove(i);
                }
            }
        }
        frame.cleanMainLog();
    }
}
