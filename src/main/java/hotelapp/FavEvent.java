package hotelapp;

public class FavEvent {
    private final String hotelid;
    private final String hotelName;
    private final String eventDate;

    public FavEvent(String hotelid, String hotelName, String eventDate) {
        this.hotelid = hotelid;
        this.hotelName = hotelName;
        this.eventDate = eventDate;
    }

    public String getHotelid() {
        return hotelid;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getEventDate() {
        return eventDate;
    }
}
