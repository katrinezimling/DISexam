package cache;

import controllers.OrderController;
import model.Order;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it. : FIX
public class OrderCache {

    // Liste med ordre
    private ArrayList<Order> orders;

    // Den tid cache skal leve i
    private long ttl;

    // Indstiller, når cachen er oprettet
    private long created;

    public OrderCache() {
        this.ttl = Config.getOrderTtl();
    }

    public ArrayList<Order> getOrders(Boolean forceUpdate) {


        // If we whis to clear cache, we can set force update.
        // Otherwise we look at the age of the cache and figure out if we should update.
        // If the list is empty we also check for new products
        if (forceUpdate
                //Created time skal være mindre end TimeMillis. TJEK OP
                || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
                || this.orders.isEmpty()) {

            // Henter ordrer fra controller, da vi ønsker at opdatere
            ArrayList<Order> orders = OrderController.getOrders();

            // Set orders for the instance and set created timestamp
            this.orders = orders;
            this.created = System.currentTimeMillis() / 1000L;
            //Tester om cache bliver brugt
            //Denne skal ikke udskrives flere gange
            System.out.println("Cache bliver ikke brugt");
        }

        // Returnerer dokumenterne
        return this.orders;
    }
}