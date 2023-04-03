/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package net.napilnik.client;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.napilnik.entitymodel.Document;
import net.napilnik.entitymodel.DocumentController;

/**
 *
 * @author alexript
 */
public class Client {

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("model");
        try (DocumentController c = new DocumentController(emf)) {
            while (true) {

                Document d = new Document(Long.toString(new Date().getTime()));
                c.create(d);
                Long id = d.getId();
                Thread.sleep(200);
                long expectExistedId = id / 4;
                Document someDoc = c.find(Document.class, expectExistedId);
                if (someDoc != null) {
                    someDoc.addChildDocument(d);
                    c.update(someDoc);
                    Thread.sleep(200);
                    List<Document> parents = c.getParents(someDoc);
                    
                }

            }
        } catch (Exception ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
