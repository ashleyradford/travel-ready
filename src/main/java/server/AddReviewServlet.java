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

        // grab hotel name and hotel id
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);

        // see if coming from a failed add review
        String error = request.getParameter("error");
        error = StringEscapeUtils.escapeHtml4(error);

        // set up velocity template
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("templates/add-review.html");
        context.put("error", error);
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
        PrintWriter out = response.getWriter();

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // get and clean parameters
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
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
        if (hotelDB.addReview(reviewid, hotelid, username, rating,
                title, text, submissionDate.toString())) {
            response.sendRedirect("/info?hotelName=" + hotelName);
        } else {
            // review was not successfully added
            response.sendRedirect("/add-review?error=failed&hotelName=" + hotelName);
        }
    }
}
