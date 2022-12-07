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

        // redirect if not logged in
        if (username == null) response.sendRedirect("/home");

        // grab and clean parameters
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        if (hotelSearch == null) hotelSearch = "";
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        if (hotelName != null) hotelName = hotelName.replaceAll("&amp;", "&");
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String error = request.getParameter("error");
        error = StringEscapeUtils.escapeHtml4(error);

        // redirect if user already submitted review to hotel
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (hotelDB.getUserReview(hotelid, username) != null) {
            if (hotelName != null) hotelName = hotelName.replaceAll("&", "%26");
            response.sendRedirect("/info?hotelSearch=" + hotelSearch + "&hotelName=" + hotelName + "&error=dup");
        }

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/add-review.html");
        context.put("username", username);
        context.put("hotelSearch", hotelSearch);
        context.put("hotelName", hotelName);
        context.put("hotelid", hotelid);
        context.put("error", error);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // redirect if not logged in
        if (username == null) response.sendRedirect("/home");

        // get and clean parameters
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        if (hotelName != null) hotelName = hotelName.replaceAll("&amp;", "%26");
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String rating = request.getParameter("rating");
        rating = StringEscapeUtils.escapeHtml4(rating);
        String title = request.getParameter("title");
        title = StringEscapeUtils.escapeHtml4(title);
        String text = request.getParameter("review-text");
        text = StringEscapeUtils.escapeHtml4(text);

        // generate review UUID and date time
        UUID reviewUUID = UUID.randomUUID();
        String reviewid = reviewUUID.toString().replaceAll("-", "");
        LocalDateTime submissionDate = LocalDateTime.now();

        // add review to database
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (hotelDB.addReview(reviewid, hotelid, username, rating, title, text, submissionDate.toString())) {
            response.sendRedirect("/info?hotelSearch=" + hotelSearch + "&hotelName=" + hotelName);
        } else { // review was not successfully added
            response.sendRedirect("/add-review?hotelSearch=" + hotelSearch + "&hotelName="
                    + hotelName + "&hotelid=" + hotelid + "&error=failed");
        }
    }
}
