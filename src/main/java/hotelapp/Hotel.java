package hotelapp;

/** Holds hotel data */
public class Hotel {
    private final String hotelid;
    private final String name;
    private final String street;
    private final String city;
    private final String state;
    private final String latitude;
    private final String longitude;

    /**
     * Constructor for Hotel class
     * @param hotelid hotel id
     * @param name hotel name
     * @param street hotel street
     * @param city hotel city
     * @param state hotel state
     * @param latitude hotel latitude
     * @param longitude hotel longitude
     */
    public Hotel(String hotelid, String name, String street, String city,
                 String state, String latitude, String longitude) {
        this.hotelid = hotelid;
        this.name = name;
        this.street = street;
        this.city = city;
        this.state = state;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getHotelid() {
        return hotelid;
    }

    public String getName() {
        return name;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    /**
     * Generates the hotel's expedia url for redirection
     * @return expedia url
     */
    public String generateExpediaUrl() {
        return "https://www.expedia.com/" + city.replaceAll(" ", "-")
                + "-Hotels-" + name.replaceAll("[^a-zA-Z\\s]", "").replaceAll("\\s+", "-")
                + ".h" + hotelid + ".Hotel-Information";
    }
}
