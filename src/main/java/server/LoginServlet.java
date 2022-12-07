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

        // clear session and redirect if user is logging out
        String status = request.getParameter("status");
        status = StringEscapeUtils.escapeHtml4(status);
        if (status != null && status.equals("end")) {
            HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
            hotelDB.setLoginTime((String) session.getAttribute("loginTime"), username);
            session.invalidate();
            response.sendRedirect("/home");
            return;
        }

        // redirect if user is already logged in
        if (username != null) response.sendRedirect("/home");

        // grab and clean parameters
        String auth = request.getParameter("auth"); // failed authentication
        auth = StringEscapeUtils.escapeHtml4(auth);

        // set up velocity template and its context
        VelocityEngine ve = (VelocityEngine) request.getServletContext().getAttribute("templateEngine");
        VelocityContext context = new VelocityContext();

        Template template = ve.getTemplate("static/login.html");
        context.put("auth", auth);

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

        LocalDateTime loginTime = LocalDateTime.now();

        // authenticate username and password
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (hotelDB.authenticateUser(username, password)) {
            // set session username
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("loginTime", loginTime.toString());
            response.sendRedirect("/home");
        } else {
            response.sendRedirect("/login?auth=failed");
        }
    }
}
