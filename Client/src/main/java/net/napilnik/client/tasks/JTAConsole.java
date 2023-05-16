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
package net.napilnik.client.tasks;

import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.client.ApplicationFrame;
import net.napilnik.client.BitronixConsole;
import net.napilnik.client.ClientTask;

/**
 *
 * @author malyshev
 */
public class JTAConsole implements ClientTask {

    @Override
    public void execute(EntityManagerFactory emf, ApplicationFrame frame) {
        try {
            BitronixConsole console = new BitronixConsole(frame.getClient() != null ? frame.getClient().getConfiguration() : null);
            frame.add(console);
        } catch (IOException ex) {
            Logger.getLogger(JTAConsole.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getTitle() {
        return "JTA Console";
    }

    @Override
    public int getWeight() {
        return 10;
    }

    @Override
    public boolean isConnectionRecuired() {
        return false;
    }

    @Override
    public boolean isDisconnectionRecuired() {
        return true;
    }

}
