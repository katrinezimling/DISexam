package com.cbsexam;
//importerer en masse klasser
import com.google.gson.Gson;
import controllers.UserController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

    // TODO: Add Encryption to JSON
    // Convert the user object to json in order to return the object
    //Bruger google bibliotek Gson, der spytter json string ud, og returnerer tilbage til brugeren igen
    String json = new Gson().toJson(user);

    //Laver kryptering:
    json = Encryption.encryptDecryptXOR(json);

    // Return the user with the status code 200
    // TODO: What should happen if something breaks down?
    return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
  }

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
    ArrayList<User> users = UserController.getUsers();

    // TODO: Add Encryption to JSON
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
  public Response loginUser(String x) {

    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Endpoint not implemented yet").build();
  }

  // TODO: Make the system able to delete users
  public Response deleteUser(String x) {

    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Endpoint not implemented yet").build();
  }

  // TODO: Make the system able to update users
  public Response updateUser(String x) {

    // Return a response with status 200 and JSON as type
    return Response.status(400).entity("Endpoint not implemented yet").build();
  }
}
