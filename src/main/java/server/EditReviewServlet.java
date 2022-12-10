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
import java.time.LocalDateTime;

public class EditReviewServlet extends HttpServlet {

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
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String error = request.getParameter("error");
        error = StringEscapeUtils.escapeHtml4(error);

        // grab the user's review for hotelid
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        Review review = hotelDB.getUserReview(hotelid, username);
        Hotel hotel = hotelDB.getHotelById(hotelid);
        String hotelName = hotel.getName();

        // get review data only if original author
        String editRating = null;
        String editTitle = null;
        String editText = null;
        if (review == null) {
            error = "unauthorized";
        } else {
            editRating = review.getRating();
            editTitle = review.getTitle();
            editText = review.getText();
        }

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/edit-review.html");
        context.put("username", username);
        context.put("hotelSearch", hotelSearch);
        context.put("hotelName", hotelName);
        context.put("hotelid", hotelid);
        context.put("editRating", editRating);
        context.put("editTitle", editTitle);
        context.put("editText", editText);
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

        // grab and clean parameters
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        if (hotelName != null) hotelName = hotelName.replaceAll("&amp;", "&");
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String modify = request.getParameter("modify");
        modify = StringEscapeUtils.escapeHtml4(modify);

        // potentially edited fields
        String editRating = request.getParameter("edit-rating");
        editRating = StringEscapeUtils.escapeHtml4(editRating);
        String editTitle = request.getParameter("edit-title");
        editTitle = StringEscapeUtils.escapeHtml4(editTitle);
        String editText = request.getParameter("edit-text");
        editText = StringEscapeUtils.escapeHtml4(editText);

        LocalDateTime editDate = LocalDateTime.now();

        // try to update or delete the user review
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (modify.equals("edit")) {
            if (!hotelDB.updateUserReview(hotelid, username, editRating, editTitle, editText, editDate.toString())) {
                // review was not successfully updated
                response.sendRedirect("/edit-review?hotelSearch=" + hotelSearch +
                        "&hotelid=" + hotelid + "&error=failedUpdate");
            }
        } else if (modify.equals("delete")) {
            if (!hotelDB.deleteUserReview(hotelid, username)) {
                // review was not successfully deleted
                response.sendRedirect("/edit-review?hotelSearch=" + hotelSearch +
                        "&hotelid=" + hotelid + "&error=failedDelete");
            }
        }

        // redirect after successfully updating or deleting review
        if (hotelName != null) hotelName = hotelName.replaceAll("&", "%26");
        response.sendRedirect("/info?hotelSearch=" + hotelSearch + "&hotelName=" + hotelName);
    }
}
