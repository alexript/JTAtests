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
package net.napilnik.server.servers;

import bitronix.tm.BitronixTransactionManager;
import bitronix.tm.TransactionManagerServices;
import net.napilnik.server.IServer;

/**
 *
 * @author malyshev
 */
public class JTAServer implements IServer {

    private BitronixTransactionManager transactionManager;

    @Override
    public void start() throws Exception {
        transactionManager = TransactionManagerServices.getTransactionManager();
    }

    @Override
    public void stop() throws Exception {
        if (transactionManager != null) {
            transactionManager.shutdown();
            transactionManager = null;
        }
    }

}
