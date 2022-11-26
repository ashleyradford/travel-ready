package hotelapp;

public class HotelParser {
    private final HotelDB hotelDB;

    /**
     * Constructor for HotelParser
     * @param hotelDB database handler
     */
    public HotelParser(HotelDB hotelDB) {
        this.hotelDB = hotelDB;
    }

    /**
     * Adds hotels to a sql database by parsing a given
     * JSON hotel file
     * @param filename path to JSON file
     */
    public void addHotels(String filename) {

    }
}
