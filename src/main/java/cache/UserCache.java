package cache;

import controllers.UserController;
import model.User;
import utils.Config;
import java.util.ArrayList;

//TODO: Build this cache and use it. : FIX
public class UserCache {

    // Liste med brugere
    private ArrayList<User> users;

    // Time cache skal leve
    private long ttl;

    // Sets when the cache has been created
    private long created;

    public UserCache() {
        this.ttl = Config.getUserTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate) {


        // If we whis to clear cache, we can set force update.
        // Otherwise we look at the age of the cache and figure out if we should update.
        // If the list is empty we also check for new users
        if (forceUpdate
                //Created time skal være mindre end TimeMillis. TJEK OP
                || ((this.created + this.ttl) <= (System.currentTimeMillis() / 1000L))
                || this.users.isEmpty()) {

            // Henter brugere fra controller, da vi vil opdatere
            ArrayList<User> users = UserController.getUsers();

            // Set products for the instance and set created timestamp
            this.users = users;
            this.created = System.currentTimeMillis() / 1000L;

        }

        // Returner brugerne
        return this.users;
    }

}
