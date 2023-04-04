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

import jakarta.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author alexript
 */
public class DocumentController extends AbstractController<Document, Long> {

//<editor-fold defaultstate="collapsed" desc="boring constructors">
    public DocumentController(EntityManagerFactory emf) {
        super(emf);
    }

    public DocumentController(String puName) {
        super(puName);
    }
//</editor-fold>

    /**
     * Get child documents.
     *
     * @param parent parent document
     * @return List of Document
     */
    public List<Document> getChildren(Document parent) {
        Map<String, Object> params = new HashMap<>();
        params.put("doc", parent);
        return query("GetChildren", params);
    }

    /**
     * Get parent documents.
     *
     * @param child child document
     * @return List of Document
     */
    public List<Document> getParents(Document child) {
        Map<String, Object> params = new HashMap<>();
        params.put("doc", child);
        return query("GetParents", params);
    }

    /**
     * Get child documents filtered by child mnemo.
     *
     * @param parent parent document
     * @param childMnemo child menmo
     * @return List of Document
     */
    public List<Document> getChildren(Document parent, String childMnemo) {
        Map<String, Object> params = new HashMap<>();
        params.put("doc", parent);
        params.put("mnemo", childMnemo);
        return query("GetChildrenWithMnemo", params);
    }

    /**
     * Get parent documents filtered by parent mnemo.
     *
     * @param child child document
     * @param parentMnemo parent mnemo
     * @return List of Document
     */
    public List<Document> getParents(Document child, String parentMnemo) {
        Map<String, Object> params = new HashMap<>();
        params.put("doc", child);
        params.put("mnemo", parentMnemo);
        return query("GetParentsWithMnemo", params);
    }

    /**
     * Get all Document filtered by mnemo.
     *
     * @param app for documents
     * @param mnemo Document mnemonic
     * @return List of Document
     */
    public List<Document> getByMnemo(Application app, String mnemo) {
        Map<String, Object> params = new HashMap<>();
        params.put("app", app);
        params.put("mnemo", mnemo);
        return query("GetByMnemo", params);
    }

    /**
     * Get all Document filtered by code.
     *
     * @param app for documents
     * @param code Document code
     * @return List of Document
     */
    public List<Document> getByCode(Application app, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("app", app);
        params.put("code", code);
        return query("GetByCode", params);
    }

    /**
     * Get all Document filtered by mnamo AND code.
     *
     * @param app for documents
     * @param mnemo Document mnemo
     * @param code Document code
     * @return List of Document
     */
    public List<Document> getByCode(Application app, String mnemo, String code) {
        Map<String, Object> params = new HashMap<>();
        params.put("app", app);
        params.put("mnemo", mnemo);
        params.put("code", code);
        return query("GetByMnemoCode", params);
    }

//<editor-fold defaultstate="collapsed" desc="interface implementation">
    @Override
    public Document find(Long pk) {
        return find(Document.class, pk);
    }

    @Override
    protected List<Document> query(String queryName, Map<String, Object> parameters) {
        return query(queryName, Document.class, parameters);
    }

    @Override
    protected Document querySingle(String queryName, Map<String, Object> parameters) {
        return querySingle(queryName, Document.class, parameters);
    }
//</editor-fold>

}
