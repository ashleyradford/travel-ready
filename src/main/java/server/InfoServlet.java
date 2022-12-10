package server;

import hotelapp.Hotel;
import hotelapp.HotelDB;
import hotelapp.Review;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

public class InfoServlet extends HttpServlet {

    private final int LIMIT = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // redirect if not logged in
        if (username == null) response.sendRedirect("/home");

        // grab and clean parameters
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        if (hotelSearch == null) hotelSearch = "";
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        if (hotelName != null) hotelName = hotelName.replaceAll("&amp;", "&");
        String page = StringEscapeUtils.escapeHtml4(request.getParameter("page"));
        String error = request.getParameter("error");
        error = StringEscapeUtils.escapeHtml4(error);

        // grab hotel data for template
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        Hotel hotel = hotelDB.getHotelByName(hotelName);

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

        // set favorite if hotel is favorited
        if (hotelDB.checkFavorite(username, hotel.getHotelid())) hotel.setFavorite();
        String avgRating = hotelDB.getAvgRating(hotelName);
        List<Review> reviewList = hotelDB.getHotelReviews(hotelName, LIMIT, (offset - 1) * LIMIT);

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/info.html");
        context.put("username", username);
        context.put("hotelSearch", hotelSearch);
        context.put("hotelName", hotelName);
        context.put("hotel", hotel);
        context.put("avgRating", avgRating);
        context.put("reviewList", reviewList);
        context.put("reviewCount", reviewCount);
        context.put("pageCount", pageCount);
        context.put("offset", offset);
        context.put("error", error);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
