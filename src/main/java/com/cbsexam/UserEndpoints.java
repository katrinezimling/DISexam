package com.cbsexam;
//importerer en masse klasser
import cache.UserCache;
import com.google.gson.Gson;
import com.sun.org.apache.regexp.internal.RE;
import controllers.UserController;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.User;
import utils.Encryption;
import utils.Log;
//De end-points, der ligger i denne klasse, hører til Path "user"
@Path("user")
public class UserEndpoints {

  /**
   * @param idUser
   * @return Responses
   */

  //Hvert endpoint er herefter defineret som en metode
  //GET er hvilket http-verbum, den skal reagere på
  @GET
  //Starter med at sige om den får nogle parametre med ind
  @Path("/{idUser}")
  public Response getUser(@PathParam("idUser") int idUser) {

    // Get the ID and use it to get the user from the controller. Får en bruger ud fra ID
    User user = UserController.getUser(idUser);

    // TODO: Add Encryption to JSON : FIX
    // Convert the user object to json in order to return the object
    //Bruger google bibliotek Gson, der spytter json string ud, og returnerer tilbage til brugeren igen
    String json = new Gson().toJson(user);

    //Laver kryptering:
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down? FIX

    try {
      if (user != null) {
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
      } else {
        return Response.status(400).entity("Kunne ikke finde brugeren").build();
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return Response.status(500).entity("Der gik noget galt").build();
    }
  }
  //Opretter objekt af UserCache, hvilket gør at klassen kan hentes.
  //Kan hentes i andre klasser, da den ligger udenfor metoden
  //static, da den skal hentes en gang og knyttes til en bestemt klasse
  //static giver adgang til klassens metode, uden at der er oprettet en instans af klassen
  static UserCache userCache = new UserCache();
  /** @return Responses */
  @GET
  @Path("/")
  //Henter alle brugere ud af systemet
  public Response getUsers() {

    // Write to log that we are here
    //Level bruges til at definerer hvor vigtig informationen er
    //Om det skal komme frem eller om det ikke er så vigtigt
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Get a list of users
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON : FIX
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);

    //Laver kryptering
    json = Encryption.encryptDecryptXOR(json);

    // Return the users with the status code 200
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/")
  //Poste til ny bruger, fx hvis man vil oprette en ny bruger
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Get the user back with the added ID and return it to the user
    String json = new Gson().toJson(createUser);

    // Return the data to the user
    if (createUser != null) {
      // Return a response with status 200 and JSON as type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system.
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    User user = new Gson().fromJson(body, User.class);
    String token = UserController.loginUser(user);

    if (token != "") {
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    } else {
      // Return a response with status 200 and JSON as type
      return Response.status(400).entity("Could not create user").build();
    }
  }

  // TODO: Make the system able to delete users : FIX
  @DELETE
  @Path("/delete")
  //body er inde i User
  public Response deleteUser(String body) {
    //Sender en token med i stedet for User
    User user = new Gson().fromJson(body, User.class);

    if (UserController.deleteUser(user.getToken())) {
      return Response.status(200).entity("Brugeren er slettet fra systemet").build();
    } else {
      return Response.status(400).entity("Brugeren kan ikke findes i systemet").build();
    }

  }
/*
  // TODO: Make the system able to update users
  @POST
  @Path("/update/")
  public Response updateUser(String token) {

    if (UserController.updateUser(token)){
      return Response.status(200).entity("Brugeren blev opdateret").build();
    } else {
    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Brugeren kan ikke findes i systemet").build();
  }
}*/}
