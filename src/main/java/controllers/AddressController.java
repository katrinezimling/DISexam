package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Address;
import utils.Log;

public class AddressController {

  private static DatabaseController dbCon;

  public AddressController() {
    dbCon = new DatabaseController();
  }

  public static Address getAddress(int id) {

    // Tjekker database forbindelsen
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Vores SQL string
    String sql = "SELECT * FROM address where id=" + id;

    // Do the query and set the initial value to null
    ResultSet rs = dbCon.query(sql);
    Address address = null;

    try {
      // Get the first row and build an address object
      if (rs.next()) {
        address =
            new Address(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("street_address"),
                rs.getString("city"),
                rs.getString("zipcode")
                );

        // Returnerer vores nytilføjede objekt
        return address;
      } else {
        System.out.println("Der blev ikke fundet nogen adresse");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Returner null hvis vi ikke kan finde noget
    return address;
  }

  public static Address createAddress(Address address) {

    // Skriver i log, at vi er kommet til dette step
    Log.writeLog(ProductController.class.getName(), address, "Actually creating a line item in DB", 0);

    // Tjekker for database forbindelse
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Indsætter produkterne i databasen
    int addressID = dbCon.insert(
        "INSERT INTO address(name, city, zipcode, street_address) VALUES('"
            + address.getName()
            + "', '"
            + address.getCity()
            + "', '"
            + address.getZipCode()
            + "', '"
            + address.getStreetAddress()
            + "')");

    if (addressID != 0) {
      // Opdaterer ID'et før det returneres (produktID)
      address.setId(addressID);
    } else{
      // Returner null hvis produktet ikke er blevet sat ind i databasen
      return null;
    }

    // Returner adresser
    return address;
  }
  
}
