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

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import org.eclipse.persistence.annotations.CascadeOnDelete;

/**
 * Application @Entity. Application have a title (mnemonic). Mnemonic is
 * PrimaryKey Application contain JSON metadata. Application contains set of
 * Documents. Metadata contain description of Documents structure
 *
 * @author alexript
 */
@Entity
@Table(
        name = "apps"
)
@NamedQuery(name = "GetAllApplications", query = "SELECT a FROM Application a")
@NamedQuery(name = "GetStruct", query = "SELECT a.structure FROM Application a WHERE a.mnemo = :mnemo")
public class Application implements Serializable {

    private static final long serialVersionUID = -424687263026392233L;

    /**
     * PrimaryKey.
     */
    @Id
    @Column(name = "mnemo", length = 50)
    private String mnemo;

    /**
     * Application structure description as JSON string
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "struct")
    private String structure;

    /**
     * Collection of Application's Documents
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "application", orphanRemoval = true)
    @CascadeOnDelete
    private Collection<Document> documents;

    public Application() {

    }

    public Application(String mnemo) {
        this.mnemo = mnemo;
    }

//<editor-fold defaultstate="collapsed" desc="accessors">
    public String getMnemo() {
        return mnemo;
    }

    public void setMnemo(String mnemo) {
        this.mnemo = mnemo;
    }

    public String getStructure() {
        return structure;
    }

    public void setStructure(String structure) {
        this.structure = structure == null ? null : structure.trim();
    }

    public Collection<Document> getDocuments() {
        if (documents == null) {
            documents = new HashSet<>();
        }
        return documents;
    }

    public void setDocuments(Collection<Document> documents) {
        this.documents = documents;
    }

    public void addDocument(Document document) {
        if (documents == null) {
            documents = new HashSet<>();
        }
        documents.add(document);
        if (!Objects.equals(document.getApplication(), this)) {
            document.setApplication(this);
        }
    }

    public void removeDocument(Document document) {
        if (documents == null) {
            documents = new HashSet<>();
        }
        documents.remove(document);
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="equals, hashCode, toString">
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Application)) {
            return false;
        }
        Application other = (Application) obj;
        return !((this.mnemo == null && other.mnemo != null) || (this.mnemo != null && !this.mnemo.equals(other.mnemo)));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.mnemo);
        return hash;
    }

    @Override
    public String toString() {
        return "net.napilnik.entitymodel.Application[ mnemo=" + mnemo + " ]";
    }
//</editor-fold>

}
