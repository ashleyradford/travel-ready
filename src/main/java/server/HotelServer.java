package server;

import hotelapp.ArgParser;
import hotelapp.HotelDB;
import hotelapp.HotelParser;
import hotelapp.ReviewParser;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
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
     * Starts the jetty server
     * @throws Exception if access failed
     */
    public void start() throws Exception {
        // jetty server
        Server server = new Server(PORT);

        // ********************** set up server handler for servlets **********************
        ServletContextHandler serverHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        // initialize Velocity
        VelocityEngine velocity = new VelocityEngine();
        velocity.init();

        // map end points to added pairs in servlets map
        for (String path : servlets.keySet()) {
            serverHandler.addServlet(servlets.get(path), path);
        }

        serverHandler.setAttribute("hotelDB", hotelDB);
        serverHandler.setAttribute("templateEngine", velocity);

        // ********************** set up resource handler for js **********************
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resourceHandler, serverHandler });
        server.setHandler(handlers);

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
        if (!argParser.addUserArguments(args)) System.exit(0);

        // grab file paths and thread counts from the user arguments
        String hotelPath = argParser.getArgValue("-hotels");
        String reviewsPath = argParser.getArgValue("-reviews");
        int threads = argParser.getArgValue("-threads") == null ? 1 : Integer.parseInt(argParser.getArgValue("-threads"));

        // create travel database tables
        HotelDB hotelDB = new HotelDB("database.properties");
        hotelDB.createTable("travel_users");
        hotelDB.createTable("travel_hotels");
        hotelDB.createTable("travel_reviews");

        // load hotel data if specified
        if (argParser.getArgValue("-hotels") != null) {
            HotelParser hotelParser = new HotelParser(hotelDB);
            hotelParser.addHotels(hotelPath);
        }

        // load review data if specified
        if (argParser.getArgValue("-reviews") != null) {
            ReviewParser reviewParser = new ReviewParser(hotelDB, threads);
            reviewParser.addReviews(reviewsPath);
        }

        // create and set up jetty server
        HotelServer hotelServer = new HotelServer(hotelDB);
        hotelServer.addServletMapping("/registration", RegistrationServlet.class.getName());
        hotelServer.addServletMapping("/login", LoginServlet.class.getName());
        hotelServer.addServletMapping("/home", HomeServlet.class.getName());
        hotelServer.addServletMapping("/info", InfoServlet.class.getName());
        hotelServer.addServletMapping("/add-review", AddReviewServlet.class.getName());
        hotelServer.addServletMapping("/edit-review", EditReviewServlet.class.getName());

        // start jetty server
        try {
            hotelServer.start();
        } catch (Exception e) {
            System.out.println("Exception when starting hotel server: " + e);
        }
    }
}
