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


    // If we whis to clear cache, we can set force update.
    // Otherwise we look at the age of the cache and figure out if we should update.
    // If the list is empty we also check for new products
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