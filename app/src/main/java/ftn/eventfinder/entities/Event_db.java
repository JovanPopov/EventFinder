
package ftn.eventfinder.entities;



import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Table(name = "Events")
@Generated("org.jsonschema2pojo")
public class Event_db extends Model {


    @Column(name = "venueId")
    private String venueId;
    @Column(name = "venueName")
    private String venueName;
    @Column(name = "venueCoverPicture")
    private String venueCoverPicture;
    @Column(name = "venueProfilePicture")
    private String venueProfilePicture;
    @Column(name = "VenueLocation", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private VenueLocation_db venueLocation_db;
    @Column(name = "eventId")
    private String eventId;
    @Column(name = "eventName")
    private String eventName;
    @Column(name = "eventCoverPicture")
    private String eventCoverPicture;
    @Expose
    @Column(name = "eventProfilePicture")
    private String eventProfilePicture;
    @Column(name = "eventDescription")
    private String eventDescription;
    @Column(name = "eventStarttime")
    private String eventStarttime;
    @Column(name = "eventDistance")
    private String eventDistance;
    @Column(name = "eventTimeFromNow")
    private Integer eventTimeFromNow;
    @Column(name = "EventStats", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private EventStats_db eventStats_db;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Event_db() {
    }

    public Event_db(String venueId, String venueName, String venueCoverPicture, String venueProfilePicture, VenueLocation_db venueLocation, String eventId, String eventName, String eventCoverPicture, String eventProfilePicture, String eventDescription, String eventStarttime, String eventDistance, Integer eventTimeFromNow, EventStats_db eventStats_db, Map<String, Object> additionalProperties) {

        this.venueId = venueId;
        this.venueName = venueName;
        this.venueCoverPicture = venueCoverPicture;
        this.venueProfilePicture = venueProfilePicture;
        this.venueLocation_db = venueLocation;
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventCoverPicture = eventCoverPicture;
        this.eventProfilePicture = eventProfilePicture;
        this.eventDescription = eventDescription;
        this.eventStarttime = eventStarttime;
        this.eventDistance = eventDistance;
        this.eventTimeFromNow = eventTimeFromNow;
        this.eventStats_db = eventStats_db;
        this.additionalProperties = additionalProperties;
    }

    /**
     *
     * @return
     *     The venueId
     */
    public String getVenueId() {
        return venueId;
    }

    /**
     *
     * @param venueId
     *     The venueId
     */
    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    /**
     *
     * @return
     *     The venueName
     */
    public String getVenueName() {
        return venueName;
    }

    /**
     *
     * @param venueName
     *     The venueName
     */
    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    /**
     *
     * @return
     *     The venueCoverPicture
     */
    public String getVenueCoverPicture() {
        return venueCoverPicture;
    }

    /**
     *
     * @param venueCoverPicture
     *     The venueCoverPicture
     */
    public void setVenueCoverPicture(String venueCoverPicture) {
        this.venueCoverPicture = venueCoverPicture;
    }

    /**
     *
     * @return
     *     The venueProfilePicture
     */
    public String getVenueProfilePicture() {
        return venueProfilePicture;
    }

    /**
     *
     * @param venueProfilePicture
     *     The venueProfilePicture
     */
    public void setVenueProfilePicture(String venueProfilePicture) {
        this.venueProfilePicture = venueProfilePicture;
    }

    /**
     *
     * @return
     *     The venueLocation
     */
    public VenueLocation_db getVenueLocation() {
        return venueLocation_db;
    }

    /**
     *
     * @param venueLocation
     *     The venueLocation
     */
    public void setVenueLocation(VenueLocation_db venueLocation_db) {
        this.venueLocation_db = venueLocation_db;
    }

    /**
     *
     * @return
     *     The eventId
     */
    public String getEventId() {
        return eventId;
    }

    /**
     *
     * @param eventId
     *     The eventId
     */
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    /**
     *
     * @return
     *     The eventName
     */
    public String getEventName() {
        return eventName;
    }

    /**
     *
     * @param eventName
     *     The eventName
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     *
     * @return
     *     The eventCoverPicture
     */
    public String getEventCoverPicture() {
        return eventCoverPicture;
    }

    /**
     *
     * @param eventCoverPicture
     *     The eventCoverPicture
     */
    public void setEventCoverPicture(String eventCoverPicture) {
        this.eventCoverPicture = eventCoverPicture;
    }

    /**
     *
     * @return
     *     The eventProfilePicture
     */
    public String getEventProfilePicture() {
        return eventProfilePicture;
    }

    /**
     *
     * @param eventProfilePicture
     *     The eventProfilePicture
     */
    public void setEventProfilePicture(String eventProfilePicture) {
        this.eventProfilePicture = eventProfilePicture;
    }

    /**
     *
     * @return
     *     The eventDescription
     */
    public String getEventDescription() {
        return eventDescription;
    }

    /**
     *
     * @param eventDescription
     *     The eventDescription
     */
    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     *
     * @return
     *     The eventStarttime
     */
    public String getEventStarttime() {
        return eventStarttime;
    }

    /**
     *
     * @param eventStarttime
     *     The eventStarttime
     */
    public void setEventStarttime(String eventStarttime) {
        this.eventStarttime = eventStarttime;
    }

    /**
     *
     * @return
     *     The eventDistance
     */
    public String getEventDistance() {
        return eventDistance;
    }

    /**
     *
     * @param eventDistance
     *     The eventDistance
     */
    public void setEventDistance(String eventDistance) {
        this.eventDistance = eventDistance;
    }

    /**
     *
     * @return
     *     The eventTimeFromNow
     */
    public Integer getEventTimeFromNow() {
        return eventTimeFromNow;
    }

    /**
     *
     * @param eventTimeFromNow
     *     The eventTimeFromNow
     */
    public void setEventTimeFromNow(Integer eventTimeFromNow) {
        this.eventTimeFromNow = eventTimeFromNow;
    }

    /**
     *
     * @return
     *     The eventStats
     */
    public EventStats_db getEventStats() {
        return eventStats_db;
    }

    /**
     *
     * @param eventStats
     *     The eventStats
     */
    public void setEventStats(EventStats_db eventStats_db) {
        this.eventStats_db = eventStats_db;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


}
