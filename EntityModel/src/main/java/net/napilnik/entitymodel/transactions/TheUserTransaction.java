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
package net.napilnik.entitymodel.transactions;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import static net.napilnik.entitymodel.AbstractController.JNDI_TRANSACTION_MANAGER;

/**
 *
 * @author malyshev
 */
public class TheUserTransaction implements TheTransaction {

    private UserTransaction ut;
    private final EntityManager em;

    public TheUserTransaction(EntityManager em) {
        this.em = em;
    }

    @Override
    public void commit() throws Exception {
        if (ut != null) {
            ut.commit();
        }
    }

    @Override
    public void begin() {
        try {
            ut = (UserTransaction) new InitialContext().lookup(JNDI_TRANSACTION_MANAGER);
            ut.begin();
            em.joinTransaction();
        } catch (NamingException | NotSupportedException | SystemException ex) {
            Logger.getLogger(TheUserTransaction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void rollback() {
        if (ut != null) {
            try {
                ut.rollback();
            } catch (IllegalStateException | SecurityException | SystemException ex) {
                Logger.getLogger(TheUserTransaction.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public boolean isActive() {
        return false;
    }

}
