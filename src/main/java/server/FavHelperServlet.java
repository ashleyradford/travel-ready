package server;

import com.google.gson.JsonObject;
import hotelapp.HotelDB;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

public class FavHelperServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        PrintWriter out = response.getWriter();

        // grab session data
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        // grab and clean parameters
        String hotelid = request.getParameter("hotelid");
        hotelid = StringEscapeUtils.escapeHtml4(hotelid);
        String clear = request.getParameter("clear");
        clear = StringEscapeUtils.escapeHtml4(clear);

        LocalDateTime eventDate = LocalDateTime.now();

        // clear favorites and redirect back to /favorite
        HotelDB hotelDB = (HotelDB) getServletContext().getAttribute("hotelDB");
        if (clear != null && clear.equals("true")) {
            hotelDB.clearUserFavorites(username);
            response.sendRedirect("/favorites");
            return;
        }

        // update fav value and delete or add favorite hotel
        JsonObject favObj = new JsonObject();
        boolean isFav = hotelDB.checkFavorite(username, hotelid);
        if (isFav) {
            hotelDB.deleteUserFavorite(username, hotelid);
            favObj.addProperty("fav", false);
        } else {
            hotelDB.addUserFavorite(username, hotelid, eventDate.toString());
            favObj.addProperty("fav", true);
        }

        out.println(favObj);
    }
}
