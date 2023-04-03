/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package net.napilnik.entitymodel;

import jakarta.persistence.EntityManagerFactory;
import java.util.List;

/**
 *
 * @author alexript
 */
public class DocumentController extends AbstractController<Document, Long> {

    public DocumentController(EntityManagerFactory emf) {
        super(emf);
    }
    
    public List<Document> getChilds(Document doc) {
        return query("GetChilds", Document.class, doc);
    }
    
    public List<Document> getParents(Document doc) {
        return query("GetParents", Document.class, doc);
    }

}
