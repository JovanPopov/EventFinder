package ftn.eventfinder.model;

/**
 * Created by Jovan on 1.7.2016.
 */
public class Tag {

    private String eventId;
    private String vanueId;
    private String value;


    public Tag(String eventId, String venueId, String value) {
        this.eventId = eventId;
        this.vanueId = venueId;
        this.value = value;
    }

    public Tag() {
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getVenueId() {
        return vanueId;
    }

    public void setVenueId(String venueId) {
        this.vanueId = venueId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
