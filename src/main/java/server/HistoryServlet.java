package server;

import hotelapp.HotelDB;
import hotelapp.LinkEvent;
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

public class HistoryServlet extends HttpServlet {

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

        // get links and add to a list
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        List<LinkEvent> linkEventList = hotelDB.getLinkEvents(username);

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/history.html");
        context.put("username", username);
        context.put("linkEvents", linkEventList);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }
}
