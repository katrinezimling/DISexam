package com.cbsexam;
//importerer en masse klasser
import cache.UserCache;
import com.google.gson.Gson;
import controllers.UserController;
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
  //Opretter objekt af UserCache, hvilket gør at klassen kan hentes.
  //Kan hentes i andre klasser, da den ligger udenfor metoden
  //static, da den skal hentes én gang og knyttes til en bestemt klasse
  //static giver adgang til klassens metode, uden at der er oprettet en instans af klassen
  static UserCache userCache = new UserCache();

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

    //Laver kryptering med XOR
    json = Encryption.encryptDecryptXOR(json);

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

  /** @return Responses */
  @GET
  @Path("/")
  //Henter alle brugere ud af systemet
  public Response getUsers() {

    // Write to log that we are here
    //Level bruges til at definerer hvor vigtig informationen er
    //Om det skal komme frem eller om det ikke er så vigtigt
    Log.writeLog(this.getClass().getName(), this, "Get all users", 0);

    // Hent listen med brugere
    ArrayList<User> users = userCache.getUsers(false);

    // TODO: Add Encryption to JSON : FIX
    // Transfer users to json in order to return it to the user
    String json = new Gson().toJson(users);

    //Laver kryptering
    json = Encryption.encryptDecryptXOR(json);

    // Returnerer et svar med en status 200 og en json som type
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }

  @POST
  @Path("/create")
  //Poste til ny bruger, fx hvis man vil oprette en ny bruger
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createUser(String body) {

    // Read the json from body and transfer it to a user class
    User newUser = new Gson().fromJson(body, User.class);

    // Use the controller to add the user
    User createUser = UserController.createUser(newUser);

    // Henter brugeren med det tilføjede ID og returnerer det
    String json = new Gson().toJson(createUser);

    // Returnerer data til brugeren
    if (createUser != null) {
      // Returnerer et svar med en status 200 og en json som type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {
      return Response.status(400).entity("Kunne ikke oprette bruger").build();
    }
  }

  // TODO: Make the system able to login users and assign them a token to use throughout the system: FIX
  @POST
  @Path("/login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response loginUser(String body) {

    //Laver json om, så det kan læses
    //Laver instans af User-klassen
    User user = new Gson().fromJson(body, User.class);
    String token = UserController.loginUser(user);

    if (token != "") {
      //Returnerer et svar med en status 200 og en json som type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(token).build();
    } else {
      // Returnerer et svar med en status 400 og en tekstbesked
      return Response.status(400).entity("Kunne ikke logge ind").build();
    }
  }

  // TODO: Make the system able to delete users : FIX
  @POST
  @Path("/delete")
  //body er inde i User
  public Response deleteUser(String body) {
    //Sender en token med i stedet for User
    User user = new Gson().fromJson(body, User.class);

    if (UserController.deleteUser(user.getToken())) {
      //Returnerer et svar med en status 200 og en json som type
      return Response.status(200).entity("Brugeren er slettet fra systemet").build();
    } else {
      return Response.status(400).entity("Brugeren kan ikke findes i systemet").build();
    }
  }

  // TODO: Make the system able to update users: FIX
  @POST
  @Path("/update")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response updateUser(String body) {

    //Laver json om, så det kan læses
    //Laver instans af User-klassen
    User user = new Gson().fromJson(body, User.class);
    //String token = UserController.updateUser(user);

   if (UserController.updateUser(user, user.getToken())){
     userCache.getUsers(true);
     //Returnerer et svar med en status 200 og en json som type
      return Response.status(200).entity("Brugeren blev opdateret").build();
    } else {
    // Returnerer et svar med en status 400 og en tekstbesked
    return Response.status(400).entity("Brugeren kan ikke findes i systemet").build();
  }
}}