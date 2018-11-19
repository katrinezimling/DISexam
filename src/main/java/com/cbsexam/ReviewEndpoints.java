package com.cbsexam;
import com.google.gson.Gson;
import controllers.ReviewController;
import java.util.ArrayList;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Review;
import utils.Encryption;

@Path("search")
public class ReviewEndpoints {

  /**
   * @param reviewTitle
   * @return Responses
   */
  @GET
  @Path("/title/{title}")
  public Response search(@PathParam("title") String reviewTitle) {

    // Kalder controller lag for at få ordrer fra databasen
    ArrayList<Review> reviews = ReviewController.searchByTitle(reviewTitle);

    // TODO: Add Encryption to JSON : FIX
    // Vi konverterer java objekt til json ved at bruge json biblioteket, som er importeret i Maven
    String json = new Gson().toJson(reviews);

    //Laver kryptering
    json = Encryption.encryptDecryptXOR(json);

    // Returnerer et svar med en status 200 og en json som type
    return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
  }


}
