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

public class RegistrationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // redirect if user is already logged in
        if (username != null) response.sendRedirect("/home");

        // grab and clean parameters
        String error = request.getParameter("error");
        error = StringEscapeUtils.escapeHtml4(error);

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/registration.html");
        context.put("error", error);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);

        // grab and clean parameters
        String username = request.getParameter("name");
        username = StringEscapeUtils.escapeHtml4(username);
        String password = request.getParameter("pass");
        password = StringEscapeUtils.escapeHtml4(password);

        // check if username and password are valid
        if (username.matches("[a-zA-Z][a-zA-Z0-9_]{2,16}")
                && password.matches("(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%&]).{8,20}")) {
            // check if username is available
            HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
            if (hotelDB.checkUsernameAvailability(username)) {
                hotelDB.addUser(username, password);
                response.sendRedirect("/login");
            } else {
                response.sendRedirect("/registration?error=dup");
            }
        } else {
            response.sendRedirect("/registration?error=failed");
        }
    }
}
