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
package net.napilnik.entitymodel;

import net.napilnik.entitymodel.transactions.TheTransaction;

/**
 *
 * @author malyshev
 * @param <ENTITY>
 * @param <PKCLASS>
 */
public abstract class ATransaction<ENTITY, PKCLASS> implements TheTransaction<ENTITY, PKCLASS> {

    private AbstractController<ENTITY, PKCLASS> controller;

    public ATransaction() {

    }

    @Override
    public void setController(AbstractController<ENTITY, PKCLASS> controller) {
        this.controller = controller;
    }

    @Override
    public boolean create(ENTITY entity) {
        if (controller != null) {
            controller.create(this, entity);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(ENTITY entity) {
        if (controller != null) {
            controller.delete(this, entity);
            return true;
        }
        return false;
    }

    @Override
    public ENTITY find(PKCLASS pk) {
        if (controller != null) {
            return controller.find(pk);
        }
        return null;
    }

    @Override
    public boolean update(ENTITY entity) {
        if (controller != null) {
            controller.update(this, entity);
            return true;
        }
        return false;
    }

}
