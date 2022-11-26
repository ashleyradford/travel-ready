package hotelapp;

public class PreparedStatements {

    // creates travel_users table
    public static final String CREATE_USERS_TABLE =
            "CREATE TABLE IF NOT EXISTS travel_users (" +
                    "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(32) NOT NULL UNIQUE, " +
                    "password CHAR(64) NOT NULL, " +
                    "usersalt CHAR(32) NOT NULL);";

    // creates travel_hotels table
    public static final String CREATE_HOTELS_TABLE =
            "CREATE TABLE IF NOT EXISTS travel_hotels (" +
                    "hotelid INTEGER PRIMARY KEY, " +
                    "name VARCHAR(50) NOT NULL UNIQUE, " +
                    "street VARCHAR(50) NOT NULL, " +
                    "city VARCHAR(50) NOT NULL, " +
                    "state VARCHAR(50) NOT NULL," +
                    "latitude VARCHAR(50) NOT NULL," +
                    "longitude VARCHAR(50) NOT NULL);";

    // creates travel_reviews table
    public static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE IF NOT EXISTS travel_reviews (" +
                    "reviewid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
                    "hotelid INTEGER NOT NULL, " +
                    "username VARCHAR(32) NOT NULL, " +
                    "rating INTEGER NOT NULL, " +
                    "title VARCHAR(50) NOT NULL, " +
                    "text VARCHAR(225) NOT NULL, " +
                    "submission_date DATE NOT NULL);";
}
