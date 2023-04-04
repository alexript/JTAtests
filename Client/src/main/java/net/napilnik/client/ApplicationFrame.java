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

import jakarta.persistence.EntityManagerFactory;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JToggleButton;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

/**
 *
 * @author malyshev
 */
public class ApplicationFrame extends javax.swing.JFrame {

    private static final long serialVersionUID = -6450492152460215683L;
    private Client client;
    private final AppProperties props;

    /**
     * Creates new form ApplicationFrame
     */
    public ApplicationFrame() {
        initComponents();
        props = new AppProperties();

    }

    private void prepare() {

        Document document = logTextPane.getDocument();
        DocumentPrintStream outPrintStream = new DocumentPrintStream(logScrollPane.getVerticalScrollBar(), document, System.out);
        System.setOut(outPrintStream);

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

        prepareTasks();
    }

    private void prepareTasks() {
        List<ClientTask> tasks = ClientTask.getTasks();
        for (ClientTask task : tasks) {
            JToggleButton btn = new JToggleButton();

            btn.setText(task.getTitle());
            btn.setFocusable(false);
            btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
            btn.addActionListener((java.awt.event.ActionEvent evt) -> {
                if (isBusy()) {
                    btn.setSelected(false);
                    return;
                }
                if (client == null) {
                    btn.setSelected(false);
                    return;
                }
                EntityManagerFactory emf = client.getEMF();

                setBusy(true);
                btn.setEnabled(false);
                new Thread(() -> {
                    task.execute(emf);
                    setBusy(false);
                    AWTThreadTools.onReady(() -> {
                        btn.setSelected(false);
                        btn.setEnabled(true);
                    });
                }).start();
            });
            tasksToolBar.add(btn);

        }
    }

    private boolean busy;

    private void setBusy(boolean busy) {
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        connectToggleButton = new javax.swing.JToggleButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        exitButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                formComponentShown(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
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
        resetLogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetLogButtonActionPerformed(evt);
            }
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

        connectToggleButton.setText("Connect");
        connectToggleButton.setFocusable(false);
        connectToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        connectToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        connectToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectToggleButtonActionPerformed(evt);
            }
        });
        tasksToolBar.add(connectToggleButton);
        tasksToolBar.add(jSeparator1);

        exitButton.setText("Exit");
        exitButton.setFocusable(false);
        exitButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        exitButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
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

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        if (isBusy()) {
            return;
        }
        if (client != null) {
            try {
                client.close();
            } catch (Exception ex) {
                Logger.getLogger(ApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        beforeExit();
        props.writeProps();
        System.exit(0);
    }//GEN-LAST:event_formWindowClosing

    private void formComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentShown
        prepare();
    }//GEN-LAST:event_formComponentShown

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        formWindowClosing(null);
    }//GEN-LAST:event_exitButtonActionPerformed

    private void connectToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectToggleButtonActionPerformed
        if (isBusy()) {
            connectToggleButton.setSelected(!connectToggleButton.isSelected());
            return;
        }
        setBusy(true);
        if (this.client != null) {
            try {
                this.client.close();
            } catch (Exception ex) {
                Logger.getLogger(ApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.client = null;
        }
        if (connectToggleButton.isSelected()) {
            this.client = new Client();
        }
        setBusy(false);
    }//GEN-LAST:event_connectToggleButtonActionPerformed

    private void resetLogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetLogButtonActionPerformed
        Document document = logTextPane.getDocument();
        try {
            document.remove(0, document.getLength());
        } catch (BadLocationException ex) {
            Logger.getLogger(ApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_resetLogButtonActionPerformed

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton connectToggleButton;
    private javax.swing.JPanel defaultLogPanel;
    private javax.swing.JLabel executionLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JTextPane logTextPane;
    private javax.swing.JToolBar logToolBar;
    private javax.swing.JTabbedPane logsTabbedPane;
    private javax.swing.JButton resetLogButton;
    private javax.swing.JToolBar tasksToolBar;
    // End of variables declaration//GEN-END:variables

}
