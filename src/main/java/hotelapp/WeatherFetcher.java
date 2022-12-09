package hotelapp;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;

public class WeatherFetcher {

    /**
     * Sends GET request to open-mateo's server and parses weather
     * from the servers response for a specified latitude and longitude
     * @param latitude latitude
     * @param longitude longitude
     * @return JSON object
     */
    public JsonObject getWeather(String latitude, String longitude) {
        String serverResponse = callAPI(latitude, longitude);

        // parses weather from server response
        serverResponse = serverResponse.substring(serverResponse.indexOf('{'), serverResponse.length() - 1);
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse(serverResponse);
        JsonObject weatherObj = jo.getAsJsonObject("current_weather");

        return weatherObj;
    }

    /**
     * Creates a secure socket to communicate with open-mateos's server,
     * sends a GET request, and gets a response as a string
     * source ~ http://www.jguru.com/faq/view.jsp?EID=32388
     * @return server's response as a string
     */
    private String callAPI(String latitude, String longitude) {
        String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude + "&current_weather=true";
        StringBuilder sb = new StringBuilder();

        SSLSocket socket = null;
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            // create socket connection for url
            URL url = new URL(urlString);
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(url.getHost(), 443); // HTTPS uses port 443

            // output stream for the socket
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            String request = getRequest(url.getHost(), url.getPath() + "?"+ url.getQuery());

            // send a request to the server and empty the buffer
            out.println(request);
            out.flush();

            // input stream for the socket
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // use input stream to read server's response
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            System.out.println("IOException while writing or reading from the socket stream: " + e);
        } finally {
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                System.out.println("IOException when trying to close the streams or socket: " + e);
            }
        }

        // convert server response to string
        return sb.toString();
    }

    /**
     * Takes a host and a string containing path/resource/query
     * and creates a string of the HTTP GET request
     * @param host url of host for request
     * @param pathResourceQuery path + resource + query for request
     * @return request as string
     */
    private String getRequest(String host, String pathResourceQuery) {
        String request = "GET " + pathResourceQuery + " HTTP/1.1" + System.lineSeparator() // GET request
                + "Host: " + host + System.lineSeparator()      // host header required for HTTP/1.1
                + "Connection: close" + System.lineSeparator()  // close connection after we fetch page
                + System.lineSeparator();
        return request;
    }
}
