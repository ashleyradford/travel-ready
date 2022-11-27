package hotelapp;

/** MySQL prepared statements for hotel data */
public class PreparedStatements {

    /** ------------------------------------ SQL MODIFICATIONS ------------------------------------ */

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
                    "name VARCHAR(100) NOT NULL UNIQUE, " +
                    "street VARCHAR(50) NOT NULL, " +
                    "city VARCHAR(50) NOT NULL, " +
                    "state VARCHAR(50) NOT NULL," +
                    "latitude VARCHAR(50) NOT NULL," +
                    "longitude VARCHAR(50) NOT NULL);";

    // creates travel_reviews table
    public static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE IF NOT EXISTS travel_reviews (" +
                    "reviewid CHAR(24) PRIMARY KEY, " +
                    "hotelid INTEGER NOT NULL, " +
                    "username VARCHAR(32) NOT NULL, " +
                    "rating INTEGER NOT NULL, " +
                    "title VARCHAR(50) NOT NULL, " +
                    "text VARCHAR(1600) NOT NULL, " +
                    "submission_date DATETIME NOT NULL," +
                    "UNIQUE KEY hotel_user (hotelid, username));";

    // inserts user to travel_users table
    public static final String INSERT_USER =
            "INSERT INTO travel_users (username, password, usersalt) " +
                    "VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE userid = userid;"; // will not trigger update

    // inserts hotel to travel_hotels table
    public static final String INSERT_HOTEL =
            "INSERT INTO travel_hotels (hotelid, name, street, city, state, latitude, longitude) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE hotelid = hotelid;"; // will not trigger update

    // inserts review to travel_reviews table
    public static final String INSERT_REVIEW =
            "INSERT INTO travel_reviews (reviewid, hotelid, username, rating, title, text, submission_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE reviewid = reviewid;"; // will not trigger update

    /** ------------------------------------ SQL QUERIES ------------------------------------ */

    // select username from travel_users (for actual registration)
    public static final String SELECT_USERNAME =
            "SELECT username FROM travel_users WHERE username = ?";

    // authenticates user
    public static final String AUTHENTICATE_USER =
            "SELECT username FROM travel_users WHERE username = ? AND password = ?";

    // gets salt for user
    public static final String SELECT_SALT =
            "SELECT usersalt FROM travel_users WHERE username = ?";
}
