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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.Parameter;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic CRUD operations for all entity controllers.
 *
 * @author alexript
 * @param <ENTITY> @Entity class
 * @param <PKCLASS> PrimaryKey class for @Entity class
 */
public abstract class AbstractController<ENTITY, PKCLASS> implements AutoCloseable {

    /**
     * Working EntityManager instance.
     */
    protected final EntityManager em;

    /**
     * Working EntityManagerFactory instance.
     */
    private final EntityManagerFactory emf;

    /**
     * Flag: true when emf created inside this object, false when emf created
     * outside this object.
     */
    private boolean incapsulatedEMF;

    /**
     * Create CRUD controller on existed EntityManagerFactory.
     *
     * @param emf EntityManagerFactory instance;
     * @throws NullPointerException when emf is null
     */
    public AbstractController(EntityManagerFactory emf) {
        if (emf == null) {
            throw new NullPointerException("EntityManagerFactory is null");
        }
        this.emf = emf;
        this.em = emf.createEntityManager();
        this.incapsulatedEMF = false;
    }

    /**
     * Create CRUD controller by PersistenceUnit name.
     *
     * @param puName PersistenceUnit name
     */
    public AbstractController(String puName) {
        this(Persistence.createEntityManagerFactory(puName));
        this.incapsulatedEMF = true;
    }

    /**
     * Implements AutoCloseable interface. When closed: close em and (when emf
     * incapsulated inside this object) emf objects.
     */
    @Override
    public void close() {
        if (em != null) {
            em.close();
        }
        if (incapsulatedEMF && emf != null) {
            emf.close();
        }
    }

    /**
     * Create and begin Transaction.
     *
     * @return started transaction
     */
    public final EntityTransaction createTransaction() {
        EntityTransaction tr = em.getTransaction();
        tr.begin();
        return tr;
    }

    /**
     * Persist @Entity object.
     *
     * @param entity @Entity object
     * @return true on success
     */
    public final boolean create(ENTITY entity) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            create(tr, entity);
            tr.commit();
        } catch (Exception ex) {
            tr.rollback();
            sqlError(ex);
            return false;
        }
        return true;
    }

    /**
     * Add @Entity persist request into transaction.
     *
     * @param tr started transaction
     * @param entity @Entity object
     */
    public final void create(EntityTransaction tr, ENTITY entity) {
        em.persist(entity);
    }

    /**
     * Remove persisted @Entity object.
     *
     * @param entity persisted @Entity object
     * @return true on success
     */
    public final boolean delete(ENTITY entity) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            delete(tr, entity);
            tr.commit();
        } catch (Exception ex) {
            tr.rollback();
            sqlError(ex);
            return false;
        }
        return true;
    }

    /**
     * Add remove persisted @Entity object request into transaction
     *
     * @param tr started transaction
     * @param entity @Entity object
     */
    public final void delete(EntityTransaction tr, ENTITY entity) {
        em.remove(entity);
    }

    /**
     * Merge persisted @Entity object.
     *
     * @param entity persisted @Entity object
     * @return true on success
     */
    public final boolean update(ENTITY entity) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            update(tr, entity);
            tr.commit();
        } catch (Exception ex) {
            tr.rollback();
            sqlError(ex);
            return false;
        }
        return true;
    }

    /**
     * Add merge persisted @Entity object request into transaction
     *
     * @param tr started transaction
     * @param entity @Entity object
     */
    public final void update(EntityTransaction tr, ENTITY entity) {
        em.merge(entity);
    }

    /**
     * Find persisted @Entity object by PrimaryKey value.
     *
     * @param entityClass class of @Entity object
     * @param pk PrimaryKey value
     * @return found @Entity object or null
     */
    protected final ENTITY find(Class<ENTITY> entityClass, PKCLASS pk) {
        try {
            return em.find(entityClass, pk);
        } catch (Exception ex) {
            sqlError(ex);
        }
        return null;
    }

    /**
     * Find persisted @Entity object by PrimaryKey value. Simplified signature
     * for ENTITY find(Class&lt;ENTITY&gt; entityClass, PKCLASS) pk). Must be
     * Overrided in extended classes.
     *
     * @param pk PrimaryKey value
     * @return found @Entity object or null
     */
    public abstract ENTITY find(PKCLASS pk);

    /**
     * Find List of @Entity objects by NamedQuery.
     *
     * @param queryName NamedQuery name
     * @param entityClass @Entity class
     * @param parameters query parameters values map. Key -- parameter name,
     * value -- value
     * @return List of @Entity objects
     */
    protected final List<ENTITY> query(String queryName, Class<ENTITY> entityClass, Map<String, Object> parameters) {
        TypedQuery<ENTITY> nq = em.createNamedQuery(queryName, entityClass);

        Set<Parameter<?>> qParams = nq.getParameters();

        if (parameters != null && parameters.size() == qParams.size()) {
            parameters.forEach((key, value) -> {
                nq.setParameter(key, value);
            });
        }
        return nq.getResultList();
    }

    /**
     * Find single @Entity object by NamedQuery.
     *
     * @param queryName NamedQuery name
     * @param entityClass @Entity class
     * @param parameters query parameters values map. Key -- parameter name,
     * value -- value
     * @return @Entity object or null if not found
     */
    protected final ENTITY querySingle(String queryName, Class<ENTITY> entityClass, Map<String, Object> parameters) {
        TypedQuery<ENTITY> nq = em.createNamedQuery(queryName, entityClass);

        Set<Parameter<?>> qParams = nq.getParameters();

        if (parameters != null && parameters.size() == qParams.size()) {
            parameters.forEach((key, value) -> {
                nq.setParameter(key, value);
            });
        }
        try {
            return nq.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        } catch (NonUniqueResultException ex) {
            sqlError(ex);
            return nq.getResultList().get(0);
        }
    }

    /**
     * Find List of @Entity objects by NamedQuery. Simplified signature for
     * List&lt;ENTITY&gt; query(String queryName, Class&lt;ENTITY&gt;
     * entityClass, Map&lt;String, Object&gt; parameters). Must be Overrided in
     * extended classes.
     *
     * @param queryName NamedQuery name
     * @param parameters query parameters values map. Key -- parameter name,
     * value -- value
     * @return List of @Entity objects
     */
    protected abstract List<ENTITY> query(String queryName, Map<String, Object> parameters);

    /**
     * Find single @Entity object by NamedQuery. Simplified signature for ENTITY
     * querySingle(String queryName, Class&lt;ENTITY&gt; entityClass,
     * Map&lt;String, Object&gt; parameters). Must be Overrided in extended
     * classes.
     *
     * @param queryName NamedQuery name
     * @param parameters query parameters values map. Key -- parameter name,
     * value -- value
     * @return @Entity object or null if not found
     */
    protected abstract ENTITY querySingle(String queryName, Map<String, Object> parameters);

    /**
     * Find List of @Entity objects by NamedQuery without parameters.
     *
     * @param queryName NamedQuery name
     * @return List of @Entity objects
     */
    protected final List<ENTITY> query(String queryName) {
        return query(queryName, null);
    }

    /**
     * Find @Entity object by NamedQuery without parameters.
     *
     * @param queryName NamedQuery name
     * @return @Entity object or null
     */
    protected final ENTITY querySingle(String queryName) {
        return querySingle(queryName, null);
    }

    /**
     * Universal SQL Errors logger for all controllers.
     *
     * @param ex Exception
     */
    protected void sqlError(Exception ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }

}
