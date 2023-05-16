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
import javax.persistence.EntityTransaction;

/**
 *
 * @author malyshev
 */
public class TheEntityTransaction implements TheTransaction<EntityManager> {

    private final EntityManager em;
    private EntityTransaction transaction;

    public TheEntityTransaction(EntityManager em) {
        this.em = em;
    }

    @Override
    public void commit() throws Exception {
        if (transaction != null) {
            transaction.commit();
        }
    }

    @Override
    public void begin() {
        this.transaction = em.getTransaction();
        this.transaction.begin();
    }

    @Override
    public void rollback() {
        if (transaction != null) {
            transaction.rollback();
        }
    }

    @Override
    public boolean isActive() {
        if (transaction == null) {
            return false;
        }
        return transaction.isActive();
    }

}
