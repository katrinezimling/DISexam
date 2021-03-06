package controllers;

import java.io.IOException;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import utils.Config;

public final class SolrController {

  private static HttpSolrClient connection;
//Opretter forbindelse
  public SolrController() {
    connection = getConnection();
  }

  /**
   *
   *
   * @return a Connection object
   */
  public static HttpSolrClient getConnection() {

    // Bygger en URL string med settings fra config
    String urlString =
        "http://"
            + Config.getSolrHost()
            + ":"
            + Config.getSolrPort()
            + "/"
            + Config.getSolrPath()
            + "/"
            + Config.getSolrCore();

    // Forbindelse til SolR
    HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
    solr.setParser(new XMLResponseParser());

    return solr;
  }

  /**
   * Do a query in SolR
   *
   * @return a ResultSet or Null if Empty
   */
  public static SolrDocumentList search(String field, String value) {

    if(connection == null)
      connection = getConnection();

    // Søger i SolR på felt og værdi
    //Søgningen er defineret i et bibliotek
    SolrQuery query = new SolrQuery();
    query.set("q", field + ":" + value);

    // Opretter en tom dokumentliste
    SolrDocumentList docList = new SolrDocumentList();

    try {
      // Søger i Solr
      QueryResponse response = connection.query(query);

      // Får resultaterne
      docList = response.getResults();

    } catch (SolrServerException e) {
      System.out.println(e.getMessage());
    } catch (IOException e) {
      e.printStackTrace();
    }

    return docList;
  }
}
