package hotelapp;

public class LinkEvent {
    private final String hotelName;
    private final String expediaLink;
    private final String eventDate;

    public LinkEvent(String hotelName, String expediaLink, String eventDate) {
        this.hotelName = hotelName;
        this.expediaLink = expediaLink;
        this.eventDate = eventDate;
    }

    public String getHotelName() {
        return hotelName;
    }

    public String getExpediaLink() {
        return expediaLink;
    }

    public String getEventDate() {
        return eventDate;
    }
}
