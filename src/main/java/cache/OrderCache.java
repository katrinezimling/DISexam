package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;
import java.util.ArrayList;

//TODO: Build this cache and use it. : FIX
public class OrderCache {

    // En liste med alle ordrer
    private ArrayList<Order> orders;

    // Den tid cache skal leve i
    private long ttl;

    // Indstiller, når cachen er oprettet
    private long created;

    //
    public OrderCache() {
        this.ttl = Config.getOrderTtl();
    }

    public ArrayList<Order> getOrders(Boolean forceUpdate) {


        // Hvis vi vil cleare cachen, kan vi sætte force update.
        // Vi kigger på cachen alder/levetid for at finde ud af om vi skal opdatere.
        // Hvis listen er tom, så tjekker vi for nye ordrer.
        if (forceUpdate
                //Created time skal være mindre end TimeMillis, før der opdateres
                //Når den er blevet cachet, skal den ikke opdatere.
                || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
                || this.orders.isEmpty()) {

            // Henter ordrer fra controller, da vi ønsker at opdatere
            ArrayList<Order> orders = OrderController.getOrders();

            // Set orders for the instance and set created timestamp
            this.orders = orders;
            this.created = System.currentTimeMillis() / 1000L;

        }

        // Returnerer arraylisten med ordrer
        return this.orders;
    }
}