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

        // hotel search if applicable
        String hotelSearch = request.getParameter("hotelSearch");
        hotelSearch = StringEscapeUtils.escapeHtml4(hotelSearch);
        if (hotelSearch == null)
            hotelSearch = "";

        // need to perform the search
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        List<String> matchedHotels = hotelDB.findHotels(hotelSearch);

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("templates/home.html");
        context.put("username", username);
        context.put("hotelSearch", hotelSearch);
        context.put("matchedHotels", matchedHotels);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
