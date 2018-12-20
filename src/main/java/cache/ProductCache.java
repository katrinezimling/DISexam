package cache;

import controllers.ProductController;
import java.util.ArrayList;
import model.Product;
import utils.Config;

public class ProductCache {

  // Liste med produkter
  private ArrayList<Product> products;

  // Den tid cache skal leve
  private long ttl;

  // Sets når cache er oprettet
  private long created;

  public ProductCache() {
    this.ttl = Config.getProductTtl();
  }

  public ArrayList<Product> getProducts(Boolean forceUpdate) {


    // Hvis vi vil cleare cachen, kan vi sætte force update.
    // Vi kigger på cachen alder/levetid for at finde ud af om vi skal opdatere.
    // Hvis listen er tom, så tjekker vi for nye ordrer.
    //Opdaterer
    if (forceUpdate

        || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
        || this.products.isEmpty()) {

      // Henter produkterne fra controller, da vi vil opdatere
      ArrayList<Product> products = ProductController.getProducts();

      // Set products for the instance and set created timestamp
      this.products = products;
      this.created = System.currentTimeMillis() / 1000L;


    }

    // Returner produkterne
    return this.products;
  }
}