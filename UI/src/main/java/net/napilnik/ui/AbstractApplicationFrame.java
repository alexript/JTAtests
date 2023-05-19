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

import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author malyshev
 */
public abstract class AbstractApplicationFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = -8254174283488904073L;
    private final AppProperties props;

    private javax.swing.JPanel defaultLogPanel;
    private javax.swing.JLabel executionLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;

    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextPane logTextPane;
    private javax.swing.JToolBar logToolBar;
    private javax.swing.JTabbedPane logsTabbedPane;
    private javax.swing.JButton resetLogButton;
    private javax.swing.JToolBar tasksToolBar;

    public AbstractApplicationFrame(String propsFile) {
        super();
        props = new AppProperties(propsFile);
        initComponents();
    }

    private void prepare() {

        Document document = logTextPane.getDocument();
        DocumentPrintStream outPrintStream = new DocumentPrintStream(logScrollPane.getVerticalScrollBar(), document, System.out);
        System.setOut(outPrintStream);

        UIJulLogger.setOut(outPrintStream);

        LoggerDispatcher logDispatcher = new LoggerDispatcher(logsTabbedPane, outPrintStream);
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

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        logsTabbedPane = new javax.swing.JTabbedPane();
        defaultLogPanel = new javax.swing.JPanel();
        logScrollPane = new javax.swing.JScrollPane();
        logTextPane = new javax.swing.JTextPane();
        logToolBar = new javax.swing.JToolBar();
        resetLogButton = new javax.swing.JButton();
        jToolBar2 = new javax.swing.JToolBar();
        executionLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        tasksToolBar = new javax.swing.JToolBar();

        exitButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPane1.setDividerLocation(500);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel2.setLayout(new java.awt.BorderLayout());

        defaultLogPanel.setLayout(new java.awt.BorderLayout());

        logTextPane.setEditable(false);
        logScrollPane.setViewportView(logTextPane);

        defaultLogPanel.add(logScrollPane, java.awt.BorderLayout.CENTER);

        logToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        logToolBar.setRollover(true);

        resetLogButton.setText("<html>&#8855;");
        resetLogButton.setToolTipText("Clear log");
        resetLogButton.setFocusable(false);
        resetLogButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        resetLogButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetLogButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            resetLogButtonActionPerformed(evt);
        });
        logToolBar.add(resetLogButton);

        defaultLogPanel.add(logToolBar, java.awt.BorderLayout.WEST);

        logsTabbedPane.addTab("Logs", defaultLogPanel);

        jPanel2.add(logsTabbedPane, java.awt.BorderLayout.CENTER);

        jToolBar2.setRollover(true);

        executionLabel.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        executionLabel.setText("●");
        executionLabel.setAlignmentY(0.0F);
        jToolBar2.add(executionLabel);

        jPanel2.add(jToolBar2, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(jPanel2);

        jPanel1.setLayout(new java.awt.BorderLayout());

        tasksToolBar.setRollover(true);

        exitButton.setText("Exit");
        exitButton.setFocusable(false);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exitButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            exitButtonActionPerformed(evt);
        });
        tasksToolBar.add(exitButton);
        tasksToolBar.add(jSeparator2);

        jPanel1.add(tasksToolBar, java.awt.BorderLayout.PAGE_START);

        jSplitPane1.setLeftComponent(jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 689, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE)
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

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        if (isBusy()) {
            return;
        }
        onWindowClosing();

        beforeExit();
        props.writeProps();
        System.exit(0);
    }

    protected abstract void onWindowClosing();

    private void formComponentShown(java.awt.event.ComponentEvent evt) {
        prepare();
    }

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {
        formWindowClosing(null);
    }

    private void resetLogButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Document document = logTextPane.getDocument();
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(AbstractApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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
