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

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceUnitTransactionType;
import static javax.persistence.spi.PersistenceUnitTransactionType.JTA;

/**
 *
 * @author malyshev
 * @param <T>
 */
public interface TheTransaction<T> {

    // usually "java:comp/UserTransaction", but eclipselink expect "java:appserver/TransactionManager" for glassfish target-server
    public static final String JNDI_TRANSACTION_MANAGER = "java:appserver/TransactionManager";

    static TheTransaction create(PersistenceUnitTransactionType transactionType, EntityManager em) {

        TheTransaction transaction;
        if (transactionType == JTA) {
            transaction = new TheUserTransaction(em);
        } else {
            transaction = new TheEntityTransaction(em);
        }
        return transaction;

    }

    void commit() throws Exception;

    void begin();

    void rollback();

    boolean isActive();
}
