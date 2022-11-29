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

        // get and clean parameters
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        String hotelName = request.getParameter("hotelName");
        hotelName = StringEscapeUtils.escapeHtml4(hotelName);
        if (hotelName != null) hotelName = hotelName.replaceAll("&amp;", "%26"); // TODO better fix?
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String originalAuthor = request.getParameter("originalAuthor");
        originalAuthor = StringEscapeUtils.escapeHtml4(originalAuthor);

        // get edit field parameters
        String editRating= request.getParameter("rating");
        editRating = StringEscapeUtils.escapeHtml4(editRating);
        String editTitle = request.getParameter("title");
        editTitle = StringEscapeUtils.escapeHtml4(editTitle);
        String editText = request.getParameter("review-text");
        editText = StringEscapeUtils.escapeHtml4(editText);

        // set up velocity template
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("templates/edit-review.html");
        context.put("hotelSearch", hotelSearch);
        context.put("hotelid", hotelid);
        context.put("hotelName", hotelName);
        context.put("originalAuthor", originalAuthor);
        context.put("username", username);
        context.put("editRating", editRating);
        context.put("editTitle", editTitle);
        context.put("editText", editText);

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
        String originalAuthor = request.getParameter("originalAuthor"); // TODO maybe could bypass this before
        originalAuthor = StringEscapeUtils.escapeHtml4(originalAuthor);

        // potentially edited fields
        String editRating = request.getParameter("edit-rating");
        editRating = StringEscapeUtils.escapeHtml4(editRating);
        String editTitle = request.getParameter("edit-title");
        editTitle = StringEscapeUtils.escapeHtml4(editTitle);
        String editText = request.getParameter("edit-text");
        editText = StringEscapeUtils.escapeHtml4(editText);

        LocalDateTime editDate = LocalDateTime.now();

        // edit review
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (username != null && username.equals(originalAuthor)) {
            if (hotelDB.updateUserReview(hotelid, username, editRating, editTitle, editText, editDate.toString())) {
                response.sendRedirect("/info?hotelSearch=" + hotelSearch + "&hotelName=" + hotelName);
            } else {
                // failed to update
            }
        } else {
            // not the original author
        }
    }
}
