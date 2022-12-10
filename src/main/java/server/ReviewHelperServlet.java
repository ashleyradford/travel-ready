package server;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Hotel;
import hotelapp.HotelDB;
import hotelapp.Review;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class ReviewHelperServlet extends HttpServlet {

    private final int LIMIT = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab and clean parameters
        String hotelid= request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String page = StringEscapeUtils.escapeHtml4(request.getParameter("page"));

        // grab hotel data for json object
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        Hotel hotel = hotelDB.getHotelById(hotelid);

        // figure out the page count
        int reviewCount = hotelDB.getReviewCount(hotel.getHotelid());
        int pageCount = reviewCount % LIMIT == 0 ? reviewCount / LIMIT : reviewCount / LIMIT + 1;
        if (pageCount == 0) pageCount = 1;

        // now set the limit and offset
        int offset = page == null ? 1 : Integer.parseInt(page);
        if (offset > pageCount)
            offset = pageCount;
        else if (offset <= 0)
            offset = 1;

        List<Review> reviewList = hotelDB.getHotelReviews(hotel.getName(), LIMIT, (offset - 1) * LIMIT);

        // add reviews to json object and then to array
        JsonArray reviewsArray = new JsonArray();
        for (Review r : reviewList) {
            JsonObject reviewObj = new JsonObject();
            reviewObj.addProperty("rating", Integer.parseInt(r.getRating()));
            reviewObj.addProperty("title", r.getTitle());
            reviewObj.addProperty("username", r.getUsername());
            reviewObj.addProperty("submissionDate", r.getSubmissionDate());
            reviewObj.addProperty("text", r.getText());
            reviewsArray.add(reviewObj);
        }

        JsonObject reviewsObj = new JsonObject();
        reviewsObj.add("reviews", reviewsArray);
        out.println(reviewsObj);
    }
}
