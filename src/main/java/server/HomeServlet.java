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
import java.util.List;

public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // grab and clean parameters
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        if (hotelSearch == null) hotelSearch = "";

        // perform the hotel search if user is logged in
        List<String> matchedHotels = null;
        String lastLogin = null;
        if (username != null) {
            HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
            matchedHotels = hotelDB.findHotelNames(hotelSearch);
            lastLogin = hotelDB.getLastLogin(username);
        }

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/home.html");
        context.put("username", username);
        context.put("hotelSearch", hotelSearch);
        context.put("matchedHotels", matchedHotels);
        context.put("lastLogin", lastLogin);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
