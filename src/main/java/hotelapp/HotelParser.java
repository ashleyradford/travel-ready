package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Class that helps parse hotel data */
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
        // check if user did not specify a hotel path
        if (filename == null) {
            return;
        }

        List<Hotel> hotelList = new ArrayList<>();
        JsonParser parser = new JsonParser();

        try (FileReader fr = new FileReader(filename)) {
            // create JSON object
            JsonObject jo = (JsonObject) parser.parse(fr);
            JsonArray hotelArr = jo.getAsJsonArray("sr");

            for (JsonElement jeH : hotelArr) {
                // create hotel JSON object
                JsonObject hotelObj = jeH.getAsJsonObject();
                String id = hotelObj.get("id").getAsString();
                String name = hotelObj.get("f").getAsString();

                // get address fields
                String street = hotelObj.get("ad").getAsString();
                String city = hotelObj.get("ci").getAsString();
                String state =  hotelObj.get("pr").getAsString();

                // get lat and long
                JsonObject llObj = hotelObj.getAsJsonObject("ll");
                String lat = llObj.get("lat").getAsString();
                String lng = llObj.get("lng").getAsString();

                Hotel hotel = new Hotel(id, name, street, city, state, lat, lng);
                hotelList.add(hotel);
            }

            // add to sql database
            hotelDB.addManyHotels(hotelList);
            System.out.println("Hotel data successfully parsed.");
        } catch (IOException e) {
            System.out.println("Could not find file: " + filename);
        }
    }
}
