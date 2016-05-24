
package ftn.eventfinder.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Event {

    private String venueId;
    private String venueName;
    private String venueCoverPicture;
    private String venueProfilePicture;
    private VenueLocation venueLocation;
    private String eventId;
    private String eventName;
    private String eventCoverPicture;
    private String eventProfilePicture;
    private String eventDescription;
    private String eventStarttime;
    private String eventDistance;
    private Integer eventTimeFromNow;
    private EventStats eventStats;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
    public VenueLocation getVenueLocation() {
        return venueLocation;
    }

    /**
     * 
     * @param venueLocation
     *     The venueLocation
     */
    public void setVenueLocation(VenueLocation venueLocation) {
        this.venueLocation = venueLocation;
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
    public EventStats getEventStats() {
        return eventStats;
    }

    /**
     * 
     * @param eventStats
     *     The eventStats
     */
    public void setEventStats(EventStats eventStats) {
        this.eventStats = eventStats;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
