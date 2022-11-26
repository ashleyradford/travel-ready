package hotelapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

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

    /**
     * Adds reviews to a sql database by recursively traversing
     * a given directory containing JSON review files
     * @param directory path to directory with reviews
     */
    public void addReviews(String directory) {

    }
}
