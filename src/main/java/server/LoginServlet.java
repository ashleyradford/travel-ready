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

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        String status = request.getParameter("session");
        status = StringEscapeUtils.escapeHtml4(status);
        if (status != null && status.equals("end")) {
            session.invalidate();
            username = null;
        }

        // user must log out first before they can log in again
        if (username != null)
            response.sendRedirect("/home");

        // check if user was redirected here because of failed authentication
        String auth = request.getParameter("auth");
        auth = StringEscapeUtils.escapeHtml4(auth);

        // user has not logged in yet
        if (auth == null)
           auth = "pending";

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/templates/login.html");
        context.put("auth", auth);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        String username = request.getParameter("name");
        username = StringEscapeUtils.escapeHtml4(username);
        String password = request.getParameter("pass");
        password = StringEscapeUtils.escapeHtml4(password);

        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (hotelDB.authenticateUser(username, password)) {
            session.setAttribute("username", username);
            response.sendRedirect("/home");
        } else {
            response.sendRedirect("/login?auth=failed");
        }
    }
}
