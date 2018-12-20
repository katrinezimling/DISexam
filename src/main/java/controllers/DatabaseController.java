package controllers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import utils.Config;

public class DatabaseController {

    private static Connection connection;

    public DatabaseController() {
        connection = getConnection();
    }

    /**
     * Get database connection
     *
     * @return a Connection object
     */
    public static Connection getConnection() {
        try {
            // Sætter database forbindelsen med data fra config
            //Config læser ind fra en tekstfil
            String url =
                    "jdbc:mysql://"
                            + Config.getDatabaseHost()
                            + ":"
                            + Config.getDatabasePort()
                            + "/"
                            + Config.getDatabaseName()
                            + "?serverTimezone=CET";

            String user = Config.getDatabaseUsername();
            String password = Config.getDatabasePassword();

            // Registrerer driver for at kunne bruge den
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());

            // Opretter en forbindelse til databasen
            connection = DriverManager.getConnection(url, user, password);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return connection;
    }

    /**
     * Do a query in the database
     *
     * @return a ResultSet or Null if Empty
     */
    public ResultSet query(String sql) {

        // Tjekker om der er forbindelse
        if (connection == null)
            connection = getConnection();


        // Vi sætter resultset til at være tomt
        ResultSet rs = null;

        try {
            // Bygger statement som en prepared statement
            PreparedStatement stmt = connection.prepareStatement(sql);

            // Her sker forespørgslen til databasen
            rs = stmt.executeQuery();

            // Return the results
            return rs;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Returnerer resultset, som i dette tilfælde er null
        return rs;
    }

    public int insert(String sql) {

        // Sætter key til 0 som en start
        int result = 0;

        // Check that we have connection
        if (connection == null)
            connection = getConnection();

        try {
            // Bygger statement på en sikker måde
            PreparedStatement statement =
                    connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            // Execute query
            result = statement.executeUpdate();

            // Henter vores nøgle tilbage for at kunne opdatere brugeren
            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Returnerer resultset, som her vil være null
        return result;
    }
}

