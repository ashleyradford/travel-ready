package server;

import com.google.gson.JsonObject;
import hotelapp.HotelDB;
import hotelapp.WeatherFetcher;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class WeatherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab and clean parameters
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);

        // get latitude and longitude from database
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        String[] latlong = hotelDB.getLatLong(hotelid);

        // fetch weather data for response
        WeatherFetcher weatherFetcher = new WeatherFetcher();
        JsonObject meteoObj  = weatherFetcher.getWeather(latlong[0], latlong[1]);

        String temp = celsiusToFahrenheit(meteoObj.get("temperature").getAsString());
        String wind = meteoObj.get("windspeed").getAsString();
        String desc = describeWeatherCode(meteoObj.get("weathercode").getAsString());

        JsonObject weatherObj = new JsonObject();
        weatherObj.addProperty("temp", temp);
        weatherObj.addProperty("wind", wind);
        weatherObj.addProperty("desc", desc);
        out.println(weatherObj);
    }

    /**
     * Converts fahrenheit to celsius
     * @param celsius temperature in celsius
     * @return temperature in fahrenheit as a string
     */
    private String celsiusToFahrenheit(String celsius) {
        double fahrenheit = Double.parseDouble(celsius) * 1.8 + 32;
        return String.format("%.2f", fahrenheit);
    }

    /**
     * Describes the weather code
     * Based on: https://open-meteo.com/en/docs
     * @param weatherCode weather code from meteo
     * @return weather description
     */
    private String describeWeatherCode(String weatherCode) {
        String desc = null;
        switch (weatherCode) {
            case "0":
                desc = "Clear sky";
                break;
            case "1":
            case "2":
            case "3":
                desc = "Mainly clear, partly cloudy, and overcast";
                break;
            case "45":
            case "48":
                desc = "Fog and depositing rime fog";
                break;
            case "51":
            case "53":
            case "55":
                desc = "Drizzle: light, moderate, and dense intensity";
                break;
            case "56":
            case "57":
                desc = "Freezing Drizzle: light and dense intensity";
                break;
            case "61":
            case "63":
            case "65":
                desc = "Rain: slight, moderate and heavy intensity";
                break;
            case "66":
            case "67":
                desc = "Freezing Rain: light and heavy intensity";
                break;
            case "71":
            case "73":
            case "75":
                desc = "Snow fall: slight, moderate, and heavy intensity";
                break;
            case "77":
                desc = "Snow grains";
                break;
            case "80":
            case "81":
            case "82":
                desc = "Rain showers: slight, moderate, and violent";
                break;
            case "85":
            case "86":
                desc = "Snow showers slight and heavy";
                break;
            case "95":
                desc = "Thunderstorm: slight or moderate";
                break;
            case "96":
            case "99":
                desc = "Thunderstorm with slight and heavy hail";
                break;
        }
        return desc;
    }
}
