package hotelapp;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class HotelDB {
    private final Properties config; // a map of properties
    private final String uri; // uri to connect to mysql using jdbc

    /**
     * Constructor for HotelDB
     * @param configPath path to database properties file
     */
    public HotelDB(String configPath) {
        this.config = loadConfig(configPath);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("username") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    }

    /**
     * Loads the config file for sql database
     * @param configPath path to database properties file
     * @return properties object
     */
    public Properties loadConfig(String configPath) {
        Properties config = new Properties();
        try (FileReader f = new FileReader(configPath)) {
            config.load(f);
        } catch (IOException e) {
            System.out.println("IOException when loading database config file: " + e);
        }
        return config;
    }

    /**
     * Creates sql table if it does not already exist
     * @param table name of table
     */
    public void createTable(String table) {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            if (table.equals("travel_users"))
                statement.executeUpdate(PreparedStatements.CREATE_USERS_TABLE);
            else if (table.equals("travel_hotels"))
                statement.executeUpdate(PreparedStatements.CREATE_HOTELS_TABLE);
            else if (table.equals("travel_reviews"))
                statement.executeUpdate(PreparedStatements.CREATE_REVIEWS_TABLE);
        } catch (SQLException e) {
            System.out.println("Unable to connect to database: " + e);
        }
    }
}
