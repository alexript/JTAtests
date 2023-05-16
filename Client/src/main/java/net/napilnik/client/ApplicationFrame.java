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

import net.napilnik.ui.AWTThreadTools;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JToggleButton;
import net.napilnik.ui.AbstractApplicationFrame;

/**
 *
 * @author malyshev
 */
public class ApplicationFrame extends AbstractApplicationFrame {

    private static final long serialVersionUID = -6450492152460215683L;
    private Client client;
    private javax.swing.JToggleButton connectToggleButton;
    private javax.swing.JToolBar.Separator jSeparator1;

    /**
     * Creates new form ApplicationFrame
     *
     * @param propsFile
     */
    public ApplicationFrame(String propsFile) {
        super(propsFile);
    }

    public Client getClient() {
        return client;
    }

    @Override
    protected void onInitComponents() {
        jSeparator1 = new javax.swing.JToolBar.Separator();
        connectToggleButton = new javax.swing.JToggleButton();

        connectToggleButton.setText("Connect");
        connectToggleButton.setFocusable(false);
        connectToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        connectToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        connectToggleButton.addActionListener((java.awt.event.ActionEvent evt) -> {
            connectToggleButtonActionPerformed(evt);
        });
        getToolBar().add(connectToggleButton);
        getToolBar().add(jSeparator1);
    }

    @Override
    protected void onWindowShow() {
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

                if (task.isConnectionRecuired() && client == null) {
                    btn.setSelected(false);
                    return;
                }

                if (task.isDisconnectionRecuired() && client != null) {
                    btn.setSelected(false);
                    return;
                }

                EntityManagerFactory emf = task.isConnectionRecuired() ? client.getEMF() : null;

                setBusy(true);
                btn.setEnabled(false);
                new Thread(() -> {
                    task.execute(emf, ApplicationFrame.this);
                    setBusy(false);
                    AWTThreadTools.onReady(() -> {
                        btn.setSelected(false);
                        btn.setEnabled(true);
                    });
                }).start();
            });
            getToolBar().add(btn);

        }
    }

    @Override
    protected void onWindowClosing() {
        if (client != null) {
            try {
                client.close();
            } catch (Exception ex) {
                Logger.getLogger(ApplicationFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void connectToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {
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
    }

}
