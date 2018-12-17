package com.cbsexam;

import cache.OrderCache;
import com.google.gson.Gson;
import controllers.OrderController;
import java.util.ArrayList;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import model.Order;
import utils.Encryption;
import utils.Log;

@Path("order")
public class OrderEndpoints {
  //Opretter objekt af OrderCache, hvilket gør at klassen kan hentes.
  //Kan hentes i andre klasser, da den ligger udenfor metoden
  static OrderCache orderCache = new OrderCache();

  /**
   * @param idOrder
   * @return Responses
   */
  @GET
  @Path("/{idOrder}")
  public Response getOrder(@PathParam("idOrder") int idOrder) {

    // Kalder controller-laget for at få ordrer fra databasen
    Order order = OrderController.getOrder(idOrder);

    // TODO: Add Encryption to JSON : FIX
    // Vi konverterer java objekt til json ved at bruge json biblioteket, som er importeret i Maven
    String json = new Gson().toJson(order);
    //Implementerer kryptering
    json = Encryption.encryptDecryptXOR(json);

    if (order != null) {
      // Returnerer et svar med en status 200 og en json som type
      return Response.status(200).type(MediaType.APPLICATION_JSON).entity(json).build();
    } else {
      return Response.status(400).entity("Kunne ikke hente ordrer").build();
    }
  }
  /** @return Responses */
  @GET
  @Path("/")
  public Response getOrders() {

    // Kalder vores controller-lag, da vi vil hente ordrer fra databasen
    //Henter metoden getOrders fra orderCache, så metoden bliver brugt
    ArrayList<Order> orders = orderCache.getOrders(false);

    // TODO: Add Encryption to JSON : FIX
    // Vi konverterer java objekt til json ved at bruge json biblioteket, som er importeret i Maven
    String json = new Gson().toJson(orders);
    //Laver kryptering
    json = Encryption.encryptDecryptXOR(json);

    // Returnerer et svar med en status 200 og en json som type
    return Response.status(200).type(MediaType.TEXT_PLAIN_TYPE).entity(json).build();
  }

  @POST
  @Path("/")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response createOrder(String body) {

    // Læser json fra body og fører det hen til order klassen
    Order newOrder = new Gson().fromJson(body, Order.class);

    // Brug controlleren til at tilføje en user
    Order createdOrder = OrderController.createOrder(newOrder);

    // Få en bruger tilbage med det tilføjede ID og returner det til brugeren
    String json = new Gson().toJson(createdOrder);

    // Returner data til brugeren
    if (createdOrder != null) {
      // Returnerer et svar med en status 200 og en json som type
      return Response.status(200).type(MediaType.APPLICATION_JSON_TYPE).entity(json).build();
    } else {

      // Returnerer et svar med status 400 og en besked i tekst
      return Response.status(400).entity("Kunne ikke oprette ordren").build();
    }
  }
}