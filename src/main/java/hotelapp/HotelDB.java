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
                case "travel_history":
                    statement.executeUpdate(PreparedStatements.CREATE_HISTORY_TABLE);
                    break;
                case "travel_favorites":
                    statement.executeUpdate(PreparedStatements.CREATE_FAVORITES_TABLE);
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
     * @param reviewid random UUID
     * @param hotelid hotel id
     * @param username nickname
     * @param rating user rating
     * @param title user title
     * @param text user review
     * @param submission_date local date time
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
                statement.setString(7, r.getSubmissionDate());
                statement.executeUpdate();
                statement.close();
            }
        } catch (SQLException e) {
            System.out.println("SQLException when adding new review: " + e);
        }
    }

    /**
     * Updates a given user review
     * @param hotelid hotel id
     * @param username username
     * @param rating edit rating
     * @param title edit title
     * @param text edit text
     * @param submissionDate local date time
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
     * Deletes a given user review
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

    /**
     * Adds a favorite hotel for a given user
     * @param username username
     * @param hotelid hotel id
     * @return true if successfully added, false otherwise
     */
    public boolean addUserFavorite(String username, String hotelid , String eventDate) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_FAVORITE);
            statement.setString(1, username);
            statement.setString(2, hotelid);
            statement.setString(3, eventDate);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when adding favorite hotel: " + e);
            return false;
        }
    }

    /**
     * Adds a link event to sql database
     * @param eventid random UUID
     * @param expediaLink expedia link
     * @param username username
     * @param eventDate local date time
     * @return true if successfully added, false otherwise
     */
    public boolean addLinkEvent(String eventid, String expediaLink, String username, int hotelid, String eventDate) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.INSERT_LINK_EVENT);
            statement.setString(1, eventid);
            statement.setString(2, expediaLink);
            statement.setString(3, username);
            statement.setInt(4, hotelid);
            statement.setString(5, eventDate);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when adding link event: " + e);
            return false;
        }
    }

    /**
     * Deletes a given user's history
     * @param username username
     * @return true if successfully deleted, false otherwise
     */
    public boolean clearUserHistory(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.CLEAR_HISTORY);
            statement.setString(1, username);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when deleting user history: " + e);
            return false;
        }
    }

    /**
     * Deletes a given user favorite
     * @param username username
     * @param hotelid hotelid
     * @return true if successfully deleted, false otherwise
     */
    public boolean deleteUserFavorite(String username, String hotelid) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.DELETE_FAVORITE);
            statement.setString(1, username);
            statement.setString(2, hotelid);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when deleting user favorite: " + e);
            return false;
        }
    }

    /**
     * Deletes all favorites of a given user
     * @param username username
     * @return true if successfully deleted, false otherwise
     */
    public boolean clearUserFavorites(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.CLEAR_FAVORITES);
            statement.setString(1, username);
            statement.executeUpdate();
            statement.close();
            return true;
        } catch (SQLException e) {
            System.out.println("SQLException when deleting all favorites: " + e);
            return false;
        }
    }

    public void setLoginTime(String loginTime, String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.UPDATE_LOGIN_TIME);
            statement.setString(1, loginTime);
            statement.setString(2, username);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            System.out.println("SQLException when setting login time: " + e);
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
    public List<String> findHotelNames(String name) {
        PreparedStatement statement;
        List<String> hotels = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_HOTEL_NAME);
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
     * Retrieves hotel data from sql database using hotel id
     * @param hotelid hotel id
     * @return Hotel object
     */
    public Hotel getHotelById(String hotelid) {
        PreparedStatement statement;
        Hotel hotel = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_HOTEL_BY_ID);
            statement.setString(1, hotelid);

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
     * Retrieves hotel data from sql database using hotel name
     * @param name hotel name
     * @return Hotel object
     */
    public Hotel getHotelByName(String name) {
        PreparedStatement statement;
        Hotel hotel = null;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_HOTEL_BY_NAME);
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
     * @param limit limit
     * @param offset offset
     * @return list of Review objects
     */
    public List<Review> getHotelReviews(String name, int limit, int offset) {
        PreparedStatement statement;
        List<Review> reviews = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_PAGE_REVIEWS);
            statement.setString(1, name);
            statement.setInt(2, limit);
            statement.setInt(3, offset);

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
     * Retrieves all link events for a given username
     * @param username username
     * @return list of link events
     */
    public List<LinkEvent> getLinkEvents(String username) {
        PreparedStatement statement;
        List<LinkEvent> linkEvents = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_USER_LINKS);
            statement.setString(1, username);

            ResultSet results = statement.executeQuery();
            while (results.next()) {
                LinkEvent linkEvent = new LinkEvent(results.getString("hotelid"),
                        results.getString("name"),
                        results.getString("expedia_link"),
                        results.getString("latest_event_date"),
                        results.getString("visit_count"));
                linkEvents.add(linkEvent);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return linkEvents;
    }

    /**
     * Retrieves all fav events for a given username
     * @param username username
     * @return list of fav events
     */
    public List<FavEvent> getFavEvents(String username) {
        PreparedStatement statement;
        List<FavEvent> favEvents = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_FAV_EVENTS);
            statement.setString(1, username);

            ResultSet results = statement.executeQuery();
            while (results.next()) {
                FavEvent favEvent = new FavEvent(results.getString("hotelid"),
                        results.getString("name"),
                        results.getString("event_date"));
                favEvents.add(favEvent);
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return favEvents;
    }

    /**
     * Gets the last login time for a given username
     * @param username username
     * @return login time as string
     */
    public String getLastLogin(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_LAST_LOGIN);
            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return results.getString("last_login");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Gets the latitude and longitude of a given hotel
     * @param hotelid hotel id
     * @return return a length 2 array of lat and long
     */
    public String[] getLatLong(String hotelid) {
        PreparedStatement statement;
        String[] latlong = new String[2];
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_LAT_LONG);
            statement.setString(1, hotelid);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                latlong[0] = results.getString("latitude");
                latlong[1] = results.getString("longitude");
                return latlong;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    /**
     * Gets the count of reviews for a given hotel id
     * @param hotelid hotel id
     * @return return count of reviews
     */
    public int getReviewCount(String hotelid) {
        PreparedStatement statement;
        int count = 0;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_REVIEW_COUNT);
            statement.setString(1, hotelid);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                count = Integer.parseInt(results.getString("review_count"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return count;
    }

    /**
     * Checks if hotel is favorited by a given user
     * @param username username
     * @param hotelid hotelid
     * @return true if favorited, false otherwise
     */
    public boolean checkFavorite(String username, String hotelid) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SELECT_FAV_HOTEL);
            statement.setString(1, username);
            statement.setString(2, hotelid);
            ResultSet results = statement.executeQuery();
            return results.next();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
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
