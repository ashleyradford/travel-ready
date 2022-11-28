package hotelapp;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/** Multithreaded class that helps parse review data */
public class ReviewParser {
    private final HotelDB hotelDB;
    private final ExecutorService poolManager;  // a pool of threads
    private final Phaser phaser;                // will keep track of tasks
    private final Logger logger = LogManager.getLogger();

    /**
     * Constructor for ReviewParser
     * @param hotelDB database handler
     * @param threads number of threads
     */
    public ReviewParser(HotelDB hotelDB, int threads) {
        this.hotelDB = hotelDB;
        this.poolManager = Executors.newFixedThreadPool(threads);
        this.phaser = new Phaser();
    }

    /** Nested class for runnable task */
    private class FileWorker implements Runnable {
        String filename;
        List<Review> localReviews;
        Set<String> localUsers;

        FileWorker(String filename) {
            this.filename = filename;
            this.localReviews = new ArrayList<>();
            this.localUsers = new HashSet<>();
        }

        @Override
        public void run() {
            try {
                logger.debug("Began working on " + filename);
                parseReviewData(filename, localReviews, localUsers);
                hotelDB.addManyReviews(localReviews);
                hotelDB.addManyUsers(localUsers);
            }
            finally {
                logger.debug("Worker is done processing " + filename);
                phaser.arriveAndDeregister();
            }
        }
    }

    /** Shuts down pool of threads */
    private void shutdownPool() {
        poolManager.shutdown();
        try {
            poolManager.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    /**
     * Adds reviews to a sql database by recursively traversing
     * a given directory containing JSON review files
     * @param directory path to directory with reviews
     */
    public void addReviews(String directory) {
        // check if user did not specify a directory
        if (directory == null) {
            return;
        }

        // check if directory does not exist
        Path path = Paths.get(directory);
        if (!Files.isDirectory(path)) {
            System.out.println("Could not open directory: " + directory);
            return;
        }

        parseDir(path); // recursively parse the directory
        phaser.awaitAdvance(phaser.getPhase()); // wait for all registered workers to arrive
        shutdownPool(); // shut down pool
        System.out.println("Review data successfully parsed.");
    }

    /**
     * Recursively parses all JSON review files in a given directory
     * @param path path to recursively traverse
     */
    private void parseDir(Path path) {
        // base case when we reach JSON file
        if (!Files.isDirectory(path) && path.toString().endsWith(".json")) {
            // create worker and submit to pool of threads
            FileWorker worker = new FileWorker(path.toString());
            logger.debug("Created a worker for " + path);
            phaser.register();
            poolManager.submit(worker);
        } else if (Files.isDirectory(path)) {
            // recursively traverse directories, until we reach a JSON file
            try (DirectoryStream<Path> pathsInDir = Files.newDirectoryStream(path)) {
                for (Path p : pathsInDir) {
                    parseDir(p);
                }
            } catch (IOException e) {
                System.out.println("Could not open directory: " + path);
                logger.error("IOException");
            }
        }
    }

    /**
     * Helper method that parses a single JSON review file
     * @param filename path to the JSON file
     * @param reviews list of review to add review to
     */
    private void parseReviewData(String filename, List<Review> reviews, Set<String> users) {
        JsonParser parser = new JsonParser();

        try (FileReader fr = new FileReader(filename)) {
            // create JSON object
            JsonObject jo = (JsonObject) parser.parse(fr);
            JsonArray reviewArr = jo.getAsJsonObject("reviewDetails")
                    .getAsJsonObject("reviewCollection")
                    .getAsJsonArray("review");

            for (JsonElement jeR : reviewArr) {
                // create review JSON object
                JsonObject hotelObj = jeR.getAsJsonObject();

                // grab relevant data
                String reviewid = hotelObj.get("reviewId").getAsString();
                String hotelid = hotelObj.get("hotelId").getAsString();
                String username = hotelObj.get("userNickname").getAsString();
                String rating = hotelObj.get("ratingOverall").getAsString();
                String title = hotelObj.get("title").getAsString().trim();
                String text = hotelObj.get("reviewText").getAsString().trim();
                String dateStr = hotelObj.get("reviewSubmissionTime").getAsString();

                // convert String submissionDate to a LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime submissionDate = LocalDateTime.parse(dateStr, formatter);

                // only register user and add review if username is valid
                if (username.matches("[a-zA-Z0-9_]{3,16}")) {
                    users.add(username);
                    Review review = new Review(reviewid, hotelid, username, rating, title, text, submissionDate.toString());
                    reviews.add(review);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not find file" + filename);
        }
    }
}
