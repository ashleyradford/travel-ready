package hotelapp;

import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import java.util.HashMap;
import java.util.Map;

public class HotelServer {

    public static final int PORT = 8080;
    private final HotelDB hotelDB;
    private final Map<String, String> servlets;

    /** Constructor for HotelServer */
    public HotelServer(HotelDB hotelDB) {
        this.hotelDB = hotelDB;
        this.servlets = new HashMap<>();
    }

    /**
     * Adds path and class name pair to servlets map
     * @param path request's path
     * @param className class name for path
     */
    public void addServletMapping(String path, String className) {
        servlets.put(path, className);
    }

    /**
     * Starts the server
     * @throws Exception throws exception if access failed
     */
    public void start() throws Exception {
        Server server = new Server(PORT); // jetty server
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        // initialize Velocity
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();

        // map end points to added pairs in servlets map
        for (String path : servlets.keySet()) {
            handler.addServlet(servlets.get(path), path);
        }

        handler.setAttribute("hotelDB", hotelDB);
        handler.setAttribute("templateEngine", velocity);
        server.setHandler(handler);

        server.start();
        server.join();
    }

    public static void main(String[] args) {
        // create argument parser
        ArgParser argParser = new ArgParser();

        // add valid arguments to argParser
        argParser.addValidArg("-hotels");
        argParser.addValidArg("-reviews");
        argParser.addValidArg("-threads");

        // exit program if user arguments are invalid
        if (!argParser.addUserArguments(args)) {
            System.exit(0);
        }

        // grab file paths and thread counts from the user arguments
        String hotelPath = argParser.getArgValue("-hotels");
        String reviewsPath = argParser.getArgValue("-reviews");
        int threads = argParser.getArgValue("-threads") == null ? 1 : Integer.parseInt(argParser.getArgValue("-threads"));

        // create and set up jetty server
        HotelDB hotelDB = new HotelDB();
        HotelServer hotelServer = new HotelServer(hotelDB);
        hotelServer.addServletMapping("/registration", RegistrationServlet.class.getName());
        hotelServer.addServletMapping("/login", LoginServlet.class.getName());
        hotelServer.addServletMapping("/home", HomeServlet.class.getName());

        // start jetty server
        try {
            hotelServer.start();
        } catch (Exception e) {
            System.out.println("Exception when starting hotel server: " + e);
        }
    }
}
