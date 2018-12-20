package controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Address;
import model.LineItem;
import model.Order;
import model.User;
import utils.Log;

public class OrderController {

    private static DatabaseController dbCon;

    public OrderController() {
        dbCon = new DatabaseController();
    }

    public static Order getOrder(int id) {

        // Tjekker om der er forbindelse til databasen
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        // Bygger SQL string til query
        String sql = "SELECT * FROM orders where id=" + id;

        // Laver query i databasen og opretter et tomt objekt til resultaterne
        ResultSet rs = dbCon.query(sql);
        Order order = null;

        try {
            if (rs.next()) {

                // Perhaps we could optimize things a bit here and get rid of nested queries.
                User user = UserController.getUser(rs.getInt("user_id"));
                ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
                Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
                Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));

                // Opretter et objekt instans af ordrer fra databasen
                order =
                        new Order(
                                rs.getInt("id"),
                                user,
                                lineItems,
                                billingAddress,
                                shippingAddress,
                                rs.getFloat("order_total"),
                                rs.getLong("created_at"),
                                rs.getLong("updated_at"));

                // Returnerer order
                return order;
            } else {
                System.out.println("Der blev ikke fundet nogle ordrer");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Returnerer null
        return order;
    }

    /**
     * Henter alle ordrer i databasen
     *
     * @return
     */
    public static ArrayList<Order> getOrders() {

        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        String sql = "SELECT * FROM orders";

        ResultSet rs = dbCon.query(sql);
        ArrayList<Order> orders = new ArrayList<Order>();

        try {
            while (rs.next()) {

                // Perhaps we could optimize things a bit here and get rid of nested queries.
                //Der skal laves 1 databasekald i stedet.

                User user = UserController.getUser(rs.getInt("user_id"));
                ArrayList<LineItem> lineItems = LineItemController.getLineItemsForOrder(rs.getInt("id"));
                Address billingAddress = AddressController.getAddress(rs.getInt("billing_address_id"));
                Address shippingAddress = AddressController.getAddress(rs.getInt("shipping_address_id"));

                // Laver en ordrer ud fra dataen i database
                Order order =
                        new Order(
                                rs.getInt("id"),
                                user,
                                lineItems,
                                billingAddress,
                                shippingAddress,
                                rs.getFloat("order_total"),
                                rs.getLong("created_at"),
                                rs.getLong("updated_at"));

                // Tilføjer ordren til listen
                orders.add(order);

            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        // Returnerer ordrerne
        return orders;
    }


    public static Order createOrder (Order order) {

        // Skriver i log, at vi er kommet til dette step
        Log.writeLog(OrderController.class.getName(), order, "Actually creating a order in DB", 0);

        // Sætter oprettelse og opdaterede tid for ordren
        order.setCreatedAt(System.currentTimeMillis() / 1000L);
        order.setUpdatedAt(System.currentTimeMillis() / 1000L);

        // Tjekker om der er forbindelse til databasen
        if (dbCon == null) {
            dbCon = new DatabaseController();
        }

        //Laver forbindelsen til databasen
        Connection connection = dbCon.getConnection();
        // TODO: Enable transactions in order for us to not save the order if somethings fails for some of the other inserts. FIX
        try {
            // Gemmer adressen i databasen og gemmer dem tilbage til det oprindelige ordre instans
            //Skal stå her, da det ellers ikke vil blive tjekket for fejl
            order.setBillingAddress(AddressController.createAddress(order.getBillingAddress()));
            order.setShippingAddress(AddressController.createAddress(order.getShippingAddress()));

            // Gemmer brugeren i databasen og gemmer tilbage til oprindelig instans
            order.setCustomer(UserController.createUser(order.getCustomer()));

            //Sætter til false, så den ikke bliver ved med at commit
            connection.setAutoCommit(false);
            // Indsætter produkterne i databasen

            int orderID = dbCon.insert(

                    "INSERT INTO orders(user_id, billing_address_id, shipping_address_id, order_total, created_at, updated_at) VALUES("
                                + order.getCustomer().getId()
                            + ", "
                            + order.getBillingAddress().getId()
                            + ", "
                            + order.getShippingAddress().getId()
                            + ", "
                            + order.calculateOrderTotal()
                            + ", "
                            + order.getCreatedAt()
                            + ", "
                            + order.getUpdatedAt()
                            + ")");

            if (orderID != 0) {
                //Opdaterer produkternes ID før der returneres
                order.setId(orderID);
            }
            // Laver en tom liste i ordre, så man kan gå igennem items og gemme dem tilbage med ID
            ArrayList<LineItem> items = new ArrayList<LineItem>();

            // Gemmer line items til databasen
            for (LineItem item : order.getLineItems()) {
                item = LineItemController.createLineItem(item, order.getId());
                items.add(item);
            }

            order.setLineItems(items);
            connection.commit();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            try {
                connection.rollback();
                System.out.println("Rollback");
            }catch (SQLException e1) {
                System.out.println("Rollback virker ikke");
            } finally {
                try {
                    connection.setAutoCommit(true);
                } catch (SQLException e2) {
                    System.out.println(e2.getMessage());
                }
            }
        }
        return order;
    }
}