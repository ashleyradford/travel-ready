package server;

import hotelapp.Hotel;
import hotelapp.HotelDB;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

public class LinkHelperServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setStatus(HttpServletResponse.SC_OK);

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // grab and clean parameters
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String clear = request.getParameter("clear");
        clear = StringEscapeUtils.escapeHtml4(clear);

        // clear history and redirect back to /history
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (clear != null && clear.equals("true")) {
            hotelDB.deleteUserHistory(username);
            response.sendRedirect("/history");
            return;
        }

        // generate event UUID and date time
        UUID eventUUID = UUID.randomUUID();
        String eventid = eventUUID.toString().replaceAll("-", "");
        LocalDateTime eventDate = LocalDateTime.now();

        // add link to database
        Hotel hotel = hotelDB.getHotelById(hotelid);
        String expediaUrl = hotel.generateExpediaUrl();
        hotelDB.addLinkEvent(eventid, expediaUrl, username, Integer.parseInt(hotelid), eventDate.toString());

        // redirect to expedia page
        response.sendRedirect(expediaUrl);
    }
}
