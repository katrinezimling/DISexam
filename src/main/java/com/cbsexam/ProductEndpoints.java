package com.cbsexam;

import cache.ProductCache;
import com.google.gson.Gson;
import controllers.DatabaseController;
import controllers.ProductController;

import java.sql.SQLException;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Product;
import utils.Encryption;
import utils.Log;

@Path("product")
public class ProductEndpoints {

  //Opretter et objekt af productcache, så klassen kan hentes
  //En static variable bliver kun instansieres/indlæst 1 gang
  static ProductCache productCache = new ProductCache();

  /**
   * @param idProduct
   * @return Responses
   */
  @GET
  @Path("/{idProduct}")
  public Response getProduct(@PathParam("idProduct") int idProduct) {

    // Kalder vores controller lag for at få ordrer fra databasen
    Product product = ProductController.getProduct(idProduct);

    // TODO: Add Encryption to JSON : FIX
    // Vi konverterer java objekt til json ved at bruge json biblioteket, som er importeret i Maven
    String json = new Gson().toJson(product);

    //Laver kryptering
    json = Encryption.encryptDecryptXOR(json);

    // Returnerer et svar med en status 200 og en json som type
    return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).entity(json).build();
  }

  /** @return Responses */
  @GET
  @Path("/")
  public Response getProducts() {

    // Kalder vores controller lag for at få ordrer fra databasen
    //Henter getProducts metoden fra ProductCache
    ArrayList<Product> products = productCache.getProducts(false);
    //True tjekker listen igennem igen for om, der er kommet ændringer


    // TODO: Add Encryption to JSON : FIX
    // Vi konverterer java objekt til json ved at bruge json biblioteket, som er importeret i Maven
    String json = new Gson().toJson(products);

    //Laver kryptering
    json = Encryption.encryptDecryptXOR(json);

    // Returnerer et svar med en status 200 og en json som type
    return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createProduct(String body) {

    // Læser json fra body og fører det hen til produkt klassen
    Product newProduct = new Gson().fromJson(body, Product.class);

    // Bruger controller til at tilføje produktet
    Product createdProduct = ProductController.createProduct(newProduct);

    // Få bruger tilbage med det tilføjede ID og returnerer det til brugeren
    String json = new Gson().toJson(createdProduct);


      // Returner data til brugeren the data to the user
      if (createdProduct != null) {
        // Returnerer et svar med en status 200 og en json som type
        return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
      } else {
        //Returnerer et svar med status 400 og en besked i tekst
        return Response.status(400).entity("Kunne ikke oprette produkt").build();
      }
    }
}