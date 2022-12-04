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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // get hotel name and search
        String error = request.getParameter("error");
        error = StringEscapeUtils.escapeHtml4(error);
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        if (hotelName != null) hotelName = hotelName.replaceAll("&amp;", "&"); // TODO better fix?

        // get hotel data for template
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        Hotel hotel = hotelDB.getHotel(hotelName);

        // get avg rating
        String avgRating = hotelDB.getAvgRating(hotelName);

        // get list of reviews
        List<Review> reviewList = hotelDB.getHotelReviews(hotelName);

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("templates/info.html");
        context.put("error", error);
        context.put("username", username);
        context.put("hotelSearch", hotelSearch);
        context.put("hotelName", hotelName);
        context.put("hotel", hotel);
        context.put("avgRating", avgRating);
        context.put("reviewList", reviewList);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
