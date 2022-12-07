package hotelapp;

public class LinkEvent {
    private final String hotelid;
    private final String hotelName;
    private final String expediaLink;
    private final String eventDate;
    private final String visitCount;

    public LinkEvent(String hotelid, String hotelName, String expediaLink, String eventDate, String visitCount) {
        this.hotelid = hotelid;
        this.hotelName = hotelName;
        this.expediaLink = expediaLink;
        this.eventDate = eventDate;
        this.visitCount = visitCount;
    }

    public String getHotelid() {
        return hotelid;
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

    public String getVisitCount() {
        return visitCount;
    }
}
