/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.napilnik.entitymodel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Parameter;
import jakarta.persistence.TypedQuery;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alexript
 * @param <ENTITY>
 */
public class AbstractController<ENTITY, PKCLASS> implements AutoCloseable {

    private final EntityManager em;

    public AbstractController(EntityManagerFactory emf) {
        this.em = emf.createEntityManager();
    }

    @Override
    public void close() throws Exception {
        em.close();
    }

    public final boolean create(ENTITY entity) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.persist(entity);
            tr.commit();
        } catch (Exception ex) {
            tr.rollback();
            sqlError(ex);
            return false;
        }
        return true;
    }

    public final boolean delete(ENTITY entity) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.remove(entity);
            tr.commit();
        } catch (Exception ex) {
            tr.rollback();
            sqlError(ex);
            return false;
        }
        return true;
    }

    public final boolean update(ENTITY entity) {
        EntityTransaction tr = em.getTransaction();
        try {
            tr.begin();
            em.merge(entity);
            tr.commit();
        } catch (Exception ex) {
            tr.rollback();
            sqlError(ex);
            return false;
        }
        return true;
    }

    public final ENTITY find(Class<ENTITY> entityClass, PKCLASS pk) {
        try {
            return em.find(entityClass, pk);
        } catch (Exception ex) {
            sqlError(ex);
        }
        return null;
    }

    public final List<ENTITY> query(String queryName, Class<ENTITY> entityClass, Object... params) {
        TypedQuery<ENTITY> nq = em.createNamedQuery(queryName, entityClass);
        
        Set<Parameter<?>> qParams = nq.getParameters();
        
        if (params != null && params.length == qParams.size()) {
            Parameter[] qpArray = qParams.toArray(new Parameter[params.length]);
            for (int i = 0; i < params.length; i++) {
                nq.setParameter(qpArray[i].getName(), params[i]);
            }
        }
        return nq.getResultList();
    }

    protected void sqlError(Exception ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
    }

}
