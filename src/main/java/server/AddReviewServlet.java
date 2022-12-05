package server;

import hotelapp.HotelDB;
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
import java.time.LocalDateTime;
import java.util.UUID;

public class AddReviewServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // for back button
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        if (hotelSearch == null) hotelSearch = "";

        // grab error, hotel name and hotel id
        String error = request.getParameter("error");
        error = StringEscapeUtils.escapeHtml4(error);
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        if (hotelName != null) hotelName = hotelName.replaceAll("&amp;", "&"); // TODO better fix?

        // first check if user already submitted review to hotel
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (hotelDB.getUserReview(hotelid, username) != null) {
            if (hotelName != null) hotelName = hotelName.replaceAll("&", "%26"); // TODO better fix?
            response.sendRedirect("/info?error=dup&hotelSearch=" + hotelSearch + "&hotelName=" + hotelName);
        }

        // set up velocity template
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/templates/add-review.html");
        context.put("error", error);
        context.put("hotelSearch", hotelSearch);
        context.put("hotelid", hotelid);
        context.put("hotelName", hotelName);
        context.put("username", username);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // get and clean parameters
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        if (hotelName != null) hotelName = hotelName.replaceAll("&amp;", "%26"); // TODO better fix?
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String rating = request.getParameter("rating");
        rating = StringEscapeUtils.escapeHtml4(rating);
        String title = request.getParameter("title");
        title = StringEscapeUtils.escapeHtml4(title);
        String text = request.getParameter("review-text");
        text = StringEscapeUtils.escapeHtml4(text);

        // generate UUID review id and date time
        UUID reviewUUID = UUID.randomUUID();
        String reviewid = reviewUUID.toString().replaceAll("-", "");
        LocalDateTime submissionDate = LocalDateTime.now();

        // add review to database
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (hotelDB.addReview(reviewid, hotelid, username, rating, title, text, submissionDate.toString())) {
            response.sendRedirect("/info?hotelSearch=" + hotelSearch + "&hotelName=" + hotelName);
        } else {
            // review was not successfully added
            response.sendRedirect("/add-review?error=failed&hotelSearch=" + hotelSearch + "&hotelName=" + hotelName);
        }
    }
}
