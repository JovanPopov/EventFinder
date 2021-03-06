
package ftn.eventfinder.entities;



import android.database.Cursor;

import com.activeandroid.Cache;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Table(name = "Events")
@Generated("org.jsonschema2pojo")
public class Event_db extends Model {



    @Column(name = "VenueLocation", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    private VenueLocation_db venueLocation_db;
    @Column(name = "eventId")
    private String eventId;
    @Column(name = "eventName")
    private String eventName;
    @Column(name = "eventCoverPicture")
    private String eventCoverPicture;
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
    @Column(name = "favourite")
    private boolean favourite;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public Event_db() {
    }

    public Event_db(VenueLocation_db venueLocation, String eventId, String eventName, String eventCoverPicture, String eventProfilePicture, String eventDescription, String eventStarttime, String eventDistance, Integer eventTimeFromNow, EventStats_db eventStats_db, Boolean favourite, Map<String, Object> additionalProperties) {


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
        this.favourite = favourite;
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

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public static Cursor fetchResultCursor() {

        String tableName = Cache.getTableInfo(Event_db.class).getTableName();

        // Query all items without any conditions

        String resultRecords = new Select(tableName + ".*, " + tableName + ".Id as _id").

                from(Event_db.class).toSql();

        // Execute query on the underlying ActiveAndroid SQLite database

        Cursor resultCursor = Cache.openDatabase().rawQuery(resultRecords, null);

        return resultCursor;

    }


    public List<Tag_db> getTags() {
        return getMany(Tag_db.class, "event");
    }

}
