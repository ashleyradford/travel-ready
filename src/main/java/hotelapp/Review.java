package hotelapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Holds review data */
public class Review {
    private final String reviewid;
    private final String hotelid;
    private final String username;
    private final String rating;
    private final String title;
    private final String text;
    private final LocalDateTime submissionDate;

    /**
     * Constructor for Review class
     * @param hotelid hotel id
     * @param username nickname
     * @param rating user rating
     * @param title user title
     * @param text user review
     * @param submissionDate submission time
     */
    public Review(String reviewid, String hotelid, String username, String rating,
                  String title, String text, String submissionDate) {
        this.reviewid = reviewid;
        this.hotelid = hotelid;
        this.username = username;
        this.rating = rating;
        this.title = title;
        this.text = text;

        // convert String submissionDate to a LocalDateTime
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        this.submissionDate = LocalDateTime.parse(submissionDate, formatter);
    }

    public String getReviewid() {
        return reviewid;
    }

    public String getHotelid() {
        return hotelid;
    }

    public String getUsername() {
        return username;
    }

    public String getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }
}
