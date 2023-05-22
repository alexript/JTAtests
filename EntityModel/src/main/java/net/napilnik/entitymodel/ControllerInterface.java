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

/**
 *
 * @author malyshev
 * @param <ENTITY>
 * @param <PKCLASS>
 */
public interface ControllerInterface<ENTITY, PKCLASS> {

    /**
     * Persist @Entity object.
     *
     * @param entity @Entity object
     * @return true on success
     */
    boolean create(ENTITY entity);

    /**
     * Remove persisted @Entity object.
     *
     * @param entity persisted @Entity object
     * @return true on success
     */
    boolean delete(ENTITY entity);

    /**
     * Find persisted @Entity object by PrimaryKey value. Simplified signature
     * for ENTITY find(Class&lt;ENTITY&gt; entityClass, PKCLASS) pk). Must be
     * Overrided in extended classes.
     *
     * @param pk PrimaryKey value
     * @return found @Entity object or null
     */
    ENTITY find(PKCLASS pk);

    /**
     * Merge persisted @Entity object.
     *
     * @param entity persisted @Entity object
     * @return true on success
     */
    boolean update(ENTITY entity);

}
