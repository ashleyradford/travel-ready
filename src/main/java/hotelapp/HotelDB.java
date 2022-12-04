package hotelapp;

import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/** MySQL database handler class for travel_users, travel_hotels, and travel_reviews tables */
public class HotelDB {
    private final Properties config; // a map of properties
    private final String uri; // uri to connect to mysql using jdbc

    /**
     * Constructor for HotelDB
     * @param configPath path to database properties file
     */
    public HotelDB(String configPath) {
        this.config = loadConfig(configPath);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("database") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
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

    // ------------------------------------ SQL MODIFICATIONS ------------------------------------ //

    /**
     * Creates sql table if it does not already exist
     * @param table name of table
     */
    public void createTable(String table) {
        Statement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.createStatement();
            switch (table) {
                case "travel_users":
                    statement.executeUpdate(PreparedStatements.CREATE_USERS_TABLE);
                    break;
                case "travel_hotels":
                    statement.executeUpdate(PreparedStatements.CREATE_HOTELS_TABLE);
                    break;
                case "travel_reviews":
                    statement.executeUpdate(PreparedStatements.CREATE_REVIEWS_TABLE);
                    break;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    /**
     * Adds user to sql database after hashing the password
     * @param username username
     * @param password password
     */
    public void addUser(String username, String password) {
        byte[] saltBytes = PasswordEncoder.generateSalt();
        String userSalt = PasswordEncoder.encodeHex(saltBytes, 32);
        String passHash = PasswordEncoder.getHash(password, userSalt);

        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_USER);
            statement.setString(1, username);
            statement.setString(2, passHash);
            statement.setString(3, userSalt);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("SQLException when adding new username: " + e);
        }
    }

    /**
     * Adds a set of usernames to sql database
     * @param usernames set of usernames
     */
    public void addManyUsers(Set<String> usernames) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            int i = 1;
            for (String username : usernames) {
                // hash password
                byte[] saltBytes = PasswordEncoder.generateSalt();
                String userSalt = PasswordEncoder.encodeHex(saltBytes, 32);
                String passHash = PasswordEncoder.getHash(username + "00" + i++ + "!", userSalt);

                statement = connection.prepareStatement(PreparedStatements.INSERT_USER);
                statement.setString(1, username);
                statement.setString(2, passHash);
                statement.setString(3, userSalt);
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println("SQLException when adding new username: " + e);
        }
    }

    /**
     * Adds hotel to sql database
     * @param hotelid hotel id
     * @param name hotel name
     * @param street hotel street
     * @param city hotel city
     * @param state hotel state
     * @param latitude hotel lat
     * @param longitude hotel long
     * @return true if successfully added, false otherwise
     */
    public boolean addHotel(String hotelid, String name, String street, String city,
                         String state, String latitude, String longitude) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_HOTEL);
            statement.setString(1, hotelid);
            statement.setString(2, name);
            statement.setString(3, street);
            statement.setString(4, city);
            statement.setString(5, state);
            statement.setString(6, latitude);
            statement.setString(7, longitude);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when adding new hotel: " + e);
            return false;
        }
    }

    /**
     * Adds a list of hotels to sql database
     * @param hotels list of hotels
     */
    public void addManyHotels(List<Hotel> hotels) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            for (Hotel h : hotels) {
                statement = connection.prepareStatement(PreparedStatements.INSERT_HOTEL);
                statement.setString(1, h.getHotelid());
                statement.setString(2, h.getName());
                statement.setString(3, h.getStreet());
                statement.setString(4, h.getCity());
                statement.setString(5, h.getState());
                statement.setString(6, h.getLatitude());
                statement.setString(7, h.getLongitude());
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println("SQLException when adding new hotel: " + e);
        }
    }

    /**
     * Adds a review to sql database
     * @param reviewid review id
     * @param hotelid hotel id
     * @param username nickname
     * @param rating user rating
     * @param title user title
     * @param text user review
     * @param submission_date user submission date
     * @return true if successfully added, false otherwise
     */
    public boolean addReview(String reviewid, String hotelid, String username, String rating,
                             String title, String text, String submission_date) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_REVIEW);
            statement.setString(1, reviewid);
            statement.setString(2, hotelid);
            statement.setString(3, username);
            statement.setString(4, rating);
            statement.setString(5, title);
            statement.setString(6, text);
            statement.setString(7, submission_date);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when adding new review: " + e);
            return false;
        }
    }

    /**
     * Adds a list of reviews to sql database
     * @param reviews list of reviews
     */
    public void addManyReviews(List<Review> reviews) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            for (Review r : reviews) {
                statement = connection.prepareStatement(PreparedStatements.INSERT_REVIEW);
                statement.setString(1, r.getReviewid());
                statement.setString(2, r.getHotelid());
                statement.setString(3, r.getUsername());
                statement.setString(4, r.getRating());
                statement.setString(5, r.getTitle());
                statement.setString(6, r.getText());
                statement.setString(7, r.getSubmissionDate().toString());
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println("SQLException when adding new review: " + e);
        }
    }

    /**
     * Updates a given users review
     * @param hotelid hotel id
     * @param username username
     * @param rating edit rating
     * @param title edit title
     * @param text edit text
     * @param submissionDate edit date
     * @return true if successfully updated, false otherwise
     */
    public boolean updateUserReview(String hotelid, String username, String rating, String title,
                                    String text, String submissionDate) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.UPDATE_REVIEW);
            statement.setString(1, rating);
            statement.setString(2, title);
            statement.setString(3, text);
            statement.setString(4, submissionDate);
            statement.setString(5, hotelid);
            statement.setString(6, username);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when updating user review: " + e);
            return false;
        }
    }

    /**
     * Updates a given users review
     * @param hotelid hotel id
     * @param username username
     * @return true if successfully deleted, false otherwise
     */
    public boolean deleteUserReview(String hotelid, String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.DELETE_REVIEW);
            statement.setString(1, hotelid);
            statement.setString(2, username);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when deleting user review: " + e);
            return false;
        }
    }

    // ------------------------------------ SQL QUERIES ------------------------------------ //

    /**
     * Checks if username is available
     * @param username username to check
     * @return true if available, false otherwise
     */
    public boolean checkUsernameAvailability(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_USERNAME);
            statement.setString(1, username);

            ResultSet results = statement.executeQuery();
            return !results.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    /**
     * Authenticates username with password
     * @param username entered username
     * @param password entered password
     * @return true if authorized, false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.AUTHENTICATE_USER);
            String usersalt = getSalt(connection, username);
            String passhash = PasswordEncoder.getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            return results.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    /**
     * Finds hotel names that match a given search keyword
     * @param name keyword
     * @return list of hotel names
     */
    public List<String> findHotels(String name) {
        PreparedStatement statement;
        List<String> hotels = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_HOTEL);
            statement.setString(1, "%" + name + "%");

            ResultSet results = statement.executeQuery();
            while (results.next()) {
                hotels.add(results.getString(1));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return hotels;
    }

    /**
     * Retrieves hotel data from sql database
     * @param name hotel name
     * @return Hotel object
     */
    public Hotel getHotel(String name) {
        PreparedStatement statement;
        Hotel hotel = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_HOTEL_DATA);
            statement.setString(1, name);

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                hotel = new Hotel(results.getString(1), // hotelid
                        results.getString(2),   // name
                        results.getString(3),   // street
                        results.getString(4),   // city
                        results.getString(5),   // state
                        results.getString(6),   // lat
                        results.getString(7));  // long
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return hotel;
    }

    /**
     * Retrieves the average rating of a hotel
     * @param name hotel name
     * @return hotel average rating
     */
    public String getAvgRating(String name) {
        PreparedStatement statement;
        String avgRating = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_AVG_RATING);
            statement.setString(1, name);

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                avgRating = results.getString(1);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return avgRating;
    }

    /**
     * Retrieves a list of reviews for a given hotel name
     * @param name hotel name
     * @return list of Review objects
     */
    public List<Review> getHotelReviews(String name) {
        PreparedStatement statement;
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_HOTEL_REVIEWS);
            statement.setString(1, name);

            ResultSet results = statement.executeQuery();
            // will still return a non-empty set if we are looking for a valid hotel,
            // we will get an all null row since we are left joining onto the hotels
            while (results.next() && results.getString(1) != null) {
                Review review= new Review(results.getString(1), // reviewid
                        results.getString(2),   // hotelid
                        results.getString(3),   // username
                        results.getString(4),   // rating
                        results.getString(5),   // title
                        results.getString(6),   // text
                        results.getString(7));  // submission_date
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return reviews;
    }

    /**
     * Retrieves the reviewid for a given hotelid and username
     * @param hotelid hotel id
     * @param username username
     * @return true if review exists, false otherwise
     */
    public Review getUserReview(String hotelid, String username) {
        PreparedStatement statement;
        Review review = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_USER_REVIEW);
            statement.setString(1, hotelid);
            statement.setString(2, username);

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                review= new Review(results.getString(1), // reviewid
                        results.getString(2),   // hotelid
                        results.getString(3),   // username
                        results.getString(4),   // rating
                        results.getString(5),   // title
                        results.getString(6),   // text
                        results.getString(7));  // submission_date
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return review;
    }

    /**
     * Gets the salt for a specific user
     * @param connection database connection
     * @param user which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     */
    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.SELECT_SALT)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("usersalt");
                return salt;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return salt;
    }
}
