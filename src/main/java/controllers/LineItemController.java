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

    // Do the query and initialize an empty list for the results
    ResultSet rs = dbCon.query(sql);
    ArrayList<LineItem> items = new ArrayList<>();

    try {

      // Loop through the results from the DB
      while (rs.next()) {

        // Construct a product base on the row data with product_id
        Product product = ProductController.getProduct(rs.getInt("product_id"));

        // Initialize an instance of the line item object
        LineItem lineItem =
            new LineItem(
                rs.getInt("id"),
                product,
                rs.getInt("quantity"),
                rs.getFloat("price"));

        // Add it to our list of items and return it
        items.add(lineItem);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Returner listen, which might be empty !! TJEK
    return items;
  }

  public static LineItem createLineItem(LineItem lineItem, int orderID) {

    // Skriver i log, at vi er kommet til dette trin
    Log.writeLog(ProductController.class.getName(), lineItem, "Actually creating a line item in DB", 0);

    // Tjekker database forbindelse
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Get the ID of the product, since the user will not send it to us.
    lineItem.getProduct().setId(ProductController.getProductBySku(lineItem.getProduct().getSku()).getId());

    // Update the ID of the product

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
