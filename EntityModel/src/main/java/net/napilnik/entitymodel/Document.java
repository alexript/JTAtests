/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.HashSet;

/**
 *
 * @author alexript
 */
@Entity
@Table(name = "documents")
@NamedQuery(name = "GetChilds", query = "SELECT d FROM Document d WHERE d.parentDocuments = :doc")
@NamedQuery(name = "GetParents", query = "SELECT d FROM Document d WHERE d.childDocuments = :doc")
public class Document implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    @Column(name = "code", length = 25, nullable = false)
    private String code;

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

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "parentDocuments")
    private Collection<Document> childDocuments;

    public Document() {
        
    }
    
    public Document(String code) {
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
    
    public final void setCode(String code) {
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
