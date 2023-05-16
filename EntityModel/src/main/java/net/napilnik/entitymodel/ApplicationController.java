/*
 * Copyright 2023 alexript.
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

import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Map;

/**
 *
 * @author alexript
 */
public class ApplicationController extends AbstractController<Application, String> {

//<editor-fold defaultstate="collapsed" desc="boring constructors">
    public ApplicationController(EntityManagerFactory emf) {
        super(emf);
    }

    public ApplicationController(String puName) {
        super(puName);
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="interface implementation">
    @Override
    public Application find(String pk) {
        return find(Application.class, pk);
    }

    @Override
    protected List<Application> query(String queryName, Map<String, Object> parameters) {
        return query(queryName, Application.class, parameters);
    }

    @Override
    protected Application querySingle(String queryName, Map<String, Object> parameters) {
        return querySingle(queryName, Application.class, parameters);
    }
//</editor-fold>

    /**
     * Get all Applications.
     *
     * @return List of Applications
     */
    public List<Application> getAll() {
        return query("GetAllApplications");
    }

    /**
     * Get Application structure by Application mnemo via NamedQuery.
     *
     * @param applicationMnemo Application mnemo
     * @return structure as JSON string or null
     */
    public String getStructure(String applicationMnemo) {
        TypedQuery<String> nq = em.createNamedQuery("GetStruct", String.class);
        nq.setParameter("mnemo", applicationMnemo);

        try {
            return nq.getSingleResult();
        } catch (Exception ex) {
            sqlError(ex);
            return null;
        }
    }

}
