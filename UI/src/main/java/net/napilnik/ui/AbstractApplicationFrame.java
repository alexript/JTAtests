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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author malyshev
 */
public abstract class AbstractApplicationFrame extends JFrame {

    private static final long serialVersionUID = -8254174283488904073L;
    private final AppProperties props;

    private JPanel defaultLogPanel;
    private JLabel executionLabel;
    private JButton exitButton;
    private JPanel jPanel1;
    private JPanel jPanel2;

    private JToolBar.Separator jSeparator2;
    private JSplitPane jSplitPane1;
    private JToolBar jToolBar2;
    private JScrollPane logScrollPane;
    private JTextPane logTextPane;
    private JToolBar logToolBar;
    private JTabbedPane logsTabbedPane;
    private JButton resetLogButton;
    private JToolBar tasksToolBar;

    private final ActionListener cleanListener;
    private final ActionListener resetListener;
    private LoggerDispatcher logDispatcher;

    public AbstractApplicationFrame(String propsFile) {
        super();
        props = new AppProperties(propsFile);
        cleanListener = (ActionEvent evt) -> {
            AbstractApplicationFrame.this.cleanMainLog();
        };
        resetListener = (ActionEvent evt) -> {
            LoggerDispatcher d = AbstractApplicationFrame.this.logDispatcher;
            if (d != null) {
                d.resetPanels();
            }
        };

        initComponents();
    }

    public LoggerDispatcher getLogsDispatcher() {
        return logDispatcher;
    }

    protected javax.swing.JTabbedPane getLogTabs() {
        return logsTabbedPane;
    }

    protected void cleanMainLog() {
        if (logTextPane != null) {
            Document document = logTextPane.getDocument();
            if (document != null) {
                try {
                    document.remove(0, document.getLength());

                } catch (BadLocationException ex) {
                    Logger.getLogger(AbstractApplicationFrame.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    private void prepare() {

        Document document = logTextPane.getDocument();
        DocumentPrintStream outPrintStream = new DocumentPrintStream(logScrollPane.getVerticalScrollBar(), document, System.out);
        System.setOut(outPrintStream);

        UIJulLogger.setOut(outPrintStream);

        logDispatcher = new LoggerDispatcher(this, outPrintStream);
        UILogger.setDispatcher(logDispatcher);

        DocumentPrintStream errPrintStream = new DocumentPrintStream(logScrollPane.getVerticalScrollBar(), document, System.err);
        System.setErr(errPrintStream);

        this.setSize(props.getSize());
        this.setLocation(props.getX(), props.getY());

        boolean isMax = props.getMaximized();
        int state = this.getExtendedState();
        if (isMax) {
            this.setExtendedState(state | JFrame.MAXIMIZED_BOTH);
        } else {
            this.setExtendedState(~((~state) | JFrame.MAXIMIZED_BOTH));
        }

        jSplitPane1.setDividerLocation(props.getDividerPosition(getDefaultDividerPosition()));
        setBusy(false);

        onWindowShow();
        outPrintStream.setConsoleOutput(false);
        errPrintStream.setConsoleOutput(false);
    }

    protected abstract void onWindowShow();

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jSplitPane1 = new JSplitPane();
        jPanel2 = new JPanel();
        logsTabbedPane = new JTabbedPane();
        defaultLogPanel = new JPanel();
        logScrollPane = new JScrollPane();
        logTextPane = new JTextPane();
        logToolBar = new JToolBar();
        resetLogButton = new JButton();
        jToolBar2 = new JToolBar();
        executionLabel = new JLabel();
        jPanel1 = new JPanel();
        tasksToolBar = new JToolBar();

        exitButton = new JButton();
        jSeparator2 = new JToolBar.Separator();

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent evt) {
                prepare();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPane1.setDividerLocation(500);
        jSplitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);

        jPanel2.setLayout(new BorderLayout());

        defaultLogPanel.setLayout(new BorderLayout());

        logTextPane.setEditable(false);
        logScrollPane.setViewportView(logTextPane);

        defaultLogPanel.add(logScrollPane, BorderLayout.CENTER);

        logToolBar.setOrientation(SwingConstants.VERTICAL);
        logToolBar.setRollover(true);

        resetLogButton.setText("<html>&#8855;");
        resetLogButton.setToolTipText("Clear log");
        resetLogButton.setFocusable(false);
        resetLogButton.setHorizontalTextPosition(SwingConstants.CENTER);
        resetLogButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        resetLogButton.addActionListener(cleanListener);
        logToolBar.add(resetLogButton);

        JButton removeAllLogButton = new JButton();
        removeAllLogButton.setText("<html>&#8416;");
        removeAllLogButton.setToolTipText("Clear all log");
        removeAllLogButton.setFocusable(false);
        removeAllLogButton.setHorizontalTextPosition(SwingConstants.CENTER);
        removeAllLogButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        removeAllLogButton.addActionListener(resetListener);
        logToolBar.add(removeAllLogButton);

        defaultLogPanel.add(logToolBar, BorderLayout.WEST);

        logsTabbedPane.addTab("Logs", defaultLogPanel);

        jPanel2.add(logsTabbedPane, BorderLayout.CENTER);

        jToolBar2.setRollover(true);

        executionLabel.setFont(new Font("Segoe UI", 0, 18)); // NOI18N
        executionLabel.setText("●");
        executionLabel.setAlignmentY(0.0F);
        jToolBar2.add(executionLabel);

        jPanel2.add(jToolBar2, BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(jPanel2);

        jPanel1.setLayout(new BorderLayout());

        tasksToolBar.setRollover(true);

        exitButton.setText("Exit");
        exitButton.setFocusable(false);
        exitButton.setHorizontalTextPosition(SwingConstants.CENTER);
        exitButton.setVerticalTextPosition(SwingConstants.BOTTOM);
        exitButton.addActionListener((ActionEvent evt) -> {
            formWindowClosing(null);
        });
        tasksToolBar.add(exitButton);
        tasksToolBar.add(jSeparator2);

        jPanel1.add(tasksToolBar, BorderLayout.PAGE_START);

        jSplitPane1.setLeftComponent(jPanel1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(jSplitPane1, GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
        );

        onInitComponents();
        pack();
    }

    protected abstract void onInitComponents();

    private boolean busy;

    protected void setBusy(boolean busy) {
        AWTThreadTools.onReady(() -> {
            if (busy) {
                tasksToolBar.setEnabled(false);
                executionLabel.setForeground(Color.RED);
            } else {
                tasksToolBar.setEnabled(true);
                executionLabel.setForeground(Color.GREEN);
            }
            this.busy = busy;
        });
    }

    public boolean isBusy() {
        return busy;
    }

    private int getDefaultDividerPosition() {
        Dimension size = this.getSize();
        int h = size.height;
        return 80 * h / 100;
    }

    private void formWindowClosing(WindowEvent evt) {
        if (isBusy()) {
            return;
        }
        onWindowClosing();

        beforeExit();
        props.writeProps();
        System.exit(0);
    }

    protected abstract void onWindowClosing();

    void beforeExit() {

        props.setDividerPosition(jSplitPane1.getDividerLocation());
        int state = this.getExtendedState();
        boolean isMax = (state & JFrame.MAXIMIZED_BOTH) != 0;
        props.setMaximized(isMax);
        if (!isMax) {
            Dimension size = this.getSize();
            int x = this.getX();
            int y = this.getY();
            props.setX(x);
            props.setY(y);
            props.setSize(size);
        }
    }

    protected javax.swing.JToolBar getToolBar() {
        return tasksToolBar;
    }

}
