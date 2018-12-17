package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import model.User;
import utils.Config;
import utils.Hashing;
import utils.Log;

public class UserController {

  private static DatabaseController dbCon;

  public UserController() {
    dbCon = new DatabaseController();
  }

  public static User getUser(int id) {

    // Tjekker for database forbindelse
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Laver query for databasen
    String sql = "SELECT * FROM user where id=" + id;

    // Laver query
    ResultSet rs = dbCon.query(sql);
    User user = null;

    try {
      // Get first object, since we only have one
      if (rs.next()) {
        user =
                new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"));

        // Returner det oprettede objekt
        return user;
      } else {
        System.out.println("Brugeren blev ikke fundet");
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Return null
    return user;
  }

  /**
   * Henter alle brugerer i databasen
   *
   * @return
   */
  public static ArrayList<User> getUsers() {

    // Tjekker for database forbindelse
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // SQL statement
    String sql = "SELECT * FROM user";

    // Do the query and initialyze an empty list for use if we don't get results
    ResultSet rs = dbCon.query(sql);
    ArrayList<User> users = new ArrayList<User>();

    try {
      // Loop through DB Data
      while (rs.next()) {
        User user =
                new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("password"),
                        rs.getString("email"));

        // Tilføjer et element til listen
        users.add(user);
      }
    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }

    // Returner listen med brugere
    return users;
  }


  public static User createUser(User user) {

    // Skriver i log at vi er nået til dette step
    Log.writeLog(UserController.class.getName(), user, "Opretter en bruger i databasen", 0);

    // Set creation time for user.
    user.setCreatedTime(System.currentTimeMillis() / 1000L);

    // Tjekker database forbindelsen
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    // Indsætter bruger i databasen
    // TODO: Hash the user password before saving it. : FIX
    int userID = dbCon.insert(
            "INSERT INTO user(first_name, last_name, password, email, created_at) VALUES('"
                    + user.getFirstname()
                    + "', '"
                    + user.getLastname()
                    + "', '"
                    + Hashing.sha(user.getPassword()) //Sha bruges i stedet for MD5.
                    + "', '"
                    + user.getEmail()
                    + "', "
                    + user.getCreatedTime()
                    + ")");

    if (userID != 0) {
      //Opdaterer userID før det returneres
      user.setId(userID);
    } else {
      // Returner null hvis brugeren ikke er blevet sat ind i databasen
      return null;
    }

    // Returner bruger
    return user;
  }

  public static String loginUser(User user) {
    if (dbCon == null) {
      dbCon = new DatabaseController();
    }
//Gør at den ved hvor den skal stoppe. Den afgrænser: '.
    String sql = "SELECT * FROM user WHERE email ='" + user.getEmail() + "'AND password='" + user.getPassword() + "'";

    dbCon.insert(sql);

    //Lav query
    ResultSet resultset = dbCon.query(sql);

    //Deklarerer uden værdi ligesom med null
    User loginUser;
    String token;

//Når man logger en bruger ind, skal man have alle informationerne med
    try {
      if (resultset.next()) {

               loginUser = new User(
                        resultset.getInt("id"),
                        resultset.getString("first_name"),
                        resultset.getString("last_name"),
                        resultset.getString("password"),
                        resultset.getString("email"));

        if (loginUser != null) {
          try {
            Algorithm algorithm = Algorithm.HMAC256(Config.getSecretKey());
            token = JWT.create()
                    .withClaim("userId", loginUser.getId())
                    .withIssuer("cbsexam")
                    .sign(algorithm);
            return token;
          } catch (JWTCreationException exception) {
            System.out.println(exception.getMessage());
            return "";
          }
        }
      } else {
        System.out.println("Brugeren kunne ikke findes");
      }
    } catch (SQLException e) {
      System.out.println(e.getMessage());
      return "";
    }
    return "";
  }

  public static boolean deleteUser(String token) {

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }
    //Decode - laver det om til noget man forstår
    DecodedJWT jwt = null;
    try {
      Algorithm algorithm = Algorithm.HMAC256(Config.getSecretKey());
      JWTVerifier verifier = JWT.require(algorithm)
              .withIssuer("cbsexam")
              .build();
      jwt = verifier.verify(token);

    } catch (JWTVerificationException exception) {
      System.out.println(exception.getMessage());
    }

    String sql = "DELETE FROM user WHERE id = " + jwt.getClaim("userId").asInt();

    return dbCon.insert(sql) == 1;

  }

  public static boolean updateUser(User user, String token) {
    Hashing hashing = new Hashing();

    if (dbCon == null) {
      dbCon = new DatabaseController();
    }

    DecodedJWT jwt = null;

    try {
      Algorithm algorithm = Algorithm.HMAC256(Config.getSecretKey());
      JWTVerifier verifier = JWT.require(algorithm)
              .withIssuer("cbsexam")
              .build();
      jwt = verifier.verify(token);

    } catch (JWTVerificationException exception) {
      System.out.println(exception.getMessage());
    }

    String sql =
            "UPDATE user SET first_name = '" + user.getFirstname() + "', last_name ='" + user.getLastname() + "', password = '" + hashing.sha(user.getPassword()) + "', email ='" + user.getEmail()
                    + "' WHERE id = " + jwt.getClaim("userId").asInt();

    // Return user/token
    //Lig med 1 fordi det er en boolean.
    return dbCon.insert(sql) == 1;
  }
}