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

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import java.io.Serializable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Some kind of Document. menmo -- mnemonics of document type. code -- unique
 * alpha-numeric document code. each document can have any number of parent and
 * child documents
 *
 * @author alexript
 */
@Entity
@Table(
        name = "documents",
        indexes = {
            @Index(columnList = "mnemo"),
            @Index(columnList = "code")
        }
)
@NamedQuery(name = "GetChildren", query = "SELECT d FROM Document d WHERE d.parentDocuments = :doc")
@NamedQuery(name = "GetParents", query = "SELECT d FROM Document d WHERE d.childDocuments = :doc")
@NamedQuery(name = "GetChildrenWithMnemo", query = "SELECT d FROM Document d WHERE d.parentDocuments = :doc AND d.mnemo = :mnemo")
@NamedQuery(name = "GetParentsWithMnemo", query = "SELECT d FROM Document d WHERE d.childDocuments = :doc AND d.mnemo = :mnemo")
@NamedQuery(name = "GetByMnemo", query = "SELECT d FROM Document d WHERE d.mnemo = :mnemo ORDER BY d.code")
public class Document implements Serializable {

    private static final long serialVersionUID = -4704534697859793297L;

    /**
     * PrimaryKey.
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Document type mnemonic.
     */
    @Basic
    @Column(name = "mnemo", length = 50, nullable = false)
    private String mnemo;

    /**
     * Document alpha-numeric code.
     */
    @Basic
    @Column(name = "code", length = 25, nullable = false)
    private String code;

    /**
     * Collection of parent documents.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "doclinks",
            joinColumns = {
                @JoinColumn(name = "childId", referencedColumnName = "id")
            },
            inverseJoinColumns = {
                @JoinColumn(name = "parentId", referencedColumnName = "id")
            }
    )
    private Collection<Document> parentDocuments;

    /**
     * Collection of child documents.
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "parentDocuments")
    private Collection<Document> childDocuments;

    public Document() {

    }

    public Document(String mnemo, String code) {
        setMnemo(mnemo);
        setCode(code);
    }

//<editor-fold defaultstate="collapsed" desc="Getters and Setters">
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    private static final String CODEMASK = "[a-zA-Z0-9\\-_]+";

    /**
     * Set Document code.
     *
     * @param code AlphaNumeric code. Also accepts '-' and '_'.
     * @throws IllegalArgumentException if code is not matches rule.
     */
    public final void setCode(String code) throws IllegalArgumentException {
        if (!code.matches(CODEMASK)) {
            throw new IllegalArgumentException("Document code agreements violation: code is '%s'. Expected format: '%s'.".formatted(code, CODEMASK));
        }
        this.code = code;
    }

    public Collection<Document> getParentDocuments() {
        if (parentDocuments == null) {
            parentDocuments = new HashSet<>();
        }
        return parentDocuments;
    }

    public void setParentDocuments(Collection<Document> parentDocuments) {
        this.parentDocuments = parentDocuments;
    }

    public void addParentDocument(Document doc) {
        if (this.parentDocuments == null) {
            this.parentDocuments = new HashSet<>();
        }
        this.parentDocuments.add(doc);
        if (!doc.getChildDocuments().contains(this)) {
            doc.addChildDocument(this);
        }
    }

    public Collection<Document> getChildDocuments() {
        if (childDocuments == null) {
            childDocuments = new HashSet<>();
        }
        return childDocuments;
    }

    public void setChildDocuments(Collection<Document> childDocuments) {
        this.childDocuments = childDocuments;
    }

    public void addChildDocument(Document doc) {
        if (this.childDocuments == null) {
            this.childDocuments = new HashSet<>();
        }
        this.childDocuments.add(doc);
        if (!doc.getParentDocuments().contains(this)) {
            doc.addParentDocument(this);
        }
    }

    public String getMnemo() {
        return mnemo;
    }

    public final void setMnemo(String mnemo) {
        this.mnemo = mnemo;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="hashCode, equals, toString">
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Document)) {
            return false;
        }
        Document other = (Document) object;
        return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
    }

    @Override
    public String toString() {
        return "net.napilnik.entitymodel.Document[ id=" + id + " ]";
    }
//</editor-fold>

}
