package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.Product;
import utils.Log;

public class ProductController {

  private static DatabaseController dbCon;

  public ProductController() {
    dbCon = new DatabaseController();
  }

  public static Product getProduct(int id) {

    // Tjekker om der er forbindelse til databasen
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // SQL query til databasen
    String sql = "SELECT * FROM product where id=" + id;

    // Run the query in the DB and make an empty object to return
    ResultSet rs = dbCon.query(sql);
    Product product = null;

    try {
      // Henter første række og opretter et objekt, som returneres
      if (rs.next()) {
        product =
            new Product(
                rs.getInt("id"),
                rs.getString("product_name"),
                rs.getString("sku"),
                rs.getFloat("price"),
                rs.getString("description"),
                rs.getInt("stock"));

        // Returnerer produkterne
        return product;
      } else {
        System.out.println("Der blev ikke fundet nogle produkter");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Returnerer tomt objekt
    return product;
  }

  public static Product getProductBySku(String sku) {

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    String sql = "SELECT * FROM product where sku='" + sku + "'";

    ResultSet rs = dbCon.query(sql);
    Product product = null;

    try {

      if (rs.next()) {
        product =
                new Product(
                        rs.getInt("id"),
                        rs.getString("product_name"),
                        rs.getString("sku"),
                        rs.getFloat("price"),
                        rs.getString("description"),
                        rs.getInt("stock"));
        return product;

      }else {
        System.out.println("Produktet blev ikke fundet");
      }

    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }
    return product;
  }

  /**
   * Henter alle produkter i databasen
   *
   * @return
   */
  public static ArrayList<Product> getProducts() {

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // TODO: Use caching layer. FIX
    //Har ændret det i Endpoint, derfor er det ikke nødvendigt her
    //Smartere at lave det i Endpoints fremfor Controller.
    String sql = "SELECT * FROM product";

    ResultSet rs = dbCon.query(sql);
    ArrayList<Product> products = new ArrayList<Product>();

    try {
      while (rs.next()) {
        Product product =
            new Product(
                rs.getInt("id"),
                rs.getString("product_name"),
                rs.getString("sku"),
                rs.getFloat("price"),
                rs.getString("description"),
                rs.getInt("stock"));

        products.add(product);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }
    return products;
  }

  public static Product createProduct(Product product) {

    // Skriver til log at vi er kommet til dette step
    Log.writeLog(ProductController.class.getName(), product, "Actually creating a product in DB", 0);

    // Sætter creation time for produktet.
    product.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Tjekker om der er forbindelse til databasen
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Indsætter produkterne i databasen
    int productID = dbCon.insert(
        "INSERT INTO product(product_name, sku, price, description, stock, created_at) VALUES('"
            + product.getName()
            + "', '"
            + product.getSku()
            + "', '"
            + product.getPrice()
            + "', '"
            + product.getDescription()
            + "', '"
            + product.getStock()
            + "', '"
            + product.getCreatedTime()
            + ")");

    if (productID != 0) {
      //Opdaterer produkt id'et før det returneres
      product.setId(productID);
    } else{
      // Returnerer null hvis produktet ikke er blevet indsat i databasen
      return null;
    }

    // Returner produkt
    return product;

  }
}