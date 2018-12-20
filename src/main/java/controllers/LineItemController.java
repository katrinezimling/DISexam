package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.LineItem;
import model.Product;
import utils.Log;

public class LineItemController {

  private static DatabaseController dbCon;

  public LineItemController() {
    dbCon = new DatabaseController();
  }

  public static ArrayList<LineItem> getLineItemsForOrder(int orderID) {

    // Tjekker database forbindelsen
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Laver SQL-kald
    String sql = "SELECT * FROM line_item where order_id=" + orderID;

    // Her laves query and initialiserer en tom liste til resultaterne
    ResultSet rs = dbCon.query(sql);
    ArrayList<LineItem> items = new ArrayList<>();

    try {

      // Loop through the results from the DB
      while (rs.next()) {

        // Konstruerer en produkt base på rækkedata med product_id
        Product product = ProductController.getProduct(rs.getInt("product_id"));

        // Initialiserer et instans af line item objektet
        LineItem lineItem =
            new LineItem(
                rs.getInt("id"),
                product,
                rs.getInt("quantity"),
                rs.getFloat("price"));

        // Tilføjer til listen og items og returnerer det 
        items.add(lineItem);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Returner listen, som måske er tom
    return items;
  }

  public static LineItem createLineItem(LineItem lineItem, int orderID) {

    // Skriver i log, at vi er kommet til dette trin
    Log.writeLog(ProductController.class.getName(), lineItem, "Actually creating a line item in DB", 0);

    // Tjekker database forbindelse
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Henter ID fra produktet, da brugeren ikke vil sende det til os
    lineItem.getProduct().setId(ProductController.getProductBySku(lineItem.getProduct().getSku()).getId());

    // Opdaterer ID på produktet

    // Indsætter lineItem i databasen
    int lineItemID = dbCon.insert(
        "INSERT INTO line_item(product_id, order_id, price, quantity) VALUES("
            + lineItem.getProduct().getId()
            + ", "
            + orderID
            + ", "
            + lineItem.getPrice()
            + ", "
            + lineItem.getQuantity()
            + ")");

    if (lineItemID != 0) {
      //Opdaterer ID før de returneres
      lineItem.setId(lineItemID);
    } else{

      // Returner null hvis de ikke er blevet indsat i databasen
      return null;
    }

    // Returner lineItem
    return lineItem;
  }
  
}
