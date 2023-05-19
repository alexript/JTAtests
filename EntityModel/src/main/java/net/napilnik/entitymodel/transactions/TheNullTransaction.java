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

/**
 *
 * @author malyshev
 */
class TheNullTransaction implements TheTransaction {

    public TheNullTransaction() {

    }

    @Override
    public void commit() throws Exception {

    }

    @Override
    public void begin() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public boolean isActive() {
        return true;
    }
}
