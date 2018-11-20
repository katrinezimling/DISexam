package controllers;

import java.util.ArrayList;
import model.Review;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public class ReviewController {

  public static ArrayList<Review> searchByTitle(String title) {

    // Vi vil gerne have en tom liste til resultaterne
    ArrayList<Review> reviews = new ArrayList<Review>();

    // Laver søgningen i controlleren
    SolrDocumentList documents = SolrController.search("title", title);

    for (SolrDocument doc : documents) {

      // Create a new review based on the SolR document
      Review r =
          new Review(
              Integer.parseInt((String) doc.getFirstValue("id")),
              (String) doc.getFirstValue("title"),
              (String) doc.getFirstValue("description"),
              (String) doc.getFirstValue("author"));

      // Tilføjer review til listen
      reviews.add(r);
    }

    // Returner listen
    return reviews;
  }


  public static ArrayList<Review> searchByID(int id) {

    // Vi vil gerne have en tom liste til resultaterne
    ArrayList<Review> reviews = new ArrayList<Review>();

    // Laver søgningen i controlleren
    SolrDocumentList documents = SolrController.search("title", Integer.toString(id));

    // Loop through the results, which are documents from SolR
    for (SolrDocument doc : documents) {
      // Laver et nyt review baseret på SolR dokumentet
      Review r =
          new Review(
              (int) doc.get("id"),
              (String) doc.get("title"),
              (String) doc.get("description"),
              (String) doc.get("author"));

      // Tilføjer review til listen
      reviews.add(r);
    }

    // Returner resultaterne
    return reviews;
  }
}
