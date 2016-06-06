
package ftn.eventfinder.entities;



import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Table(name = "EventStats")
@Generated("org.jsonschema2pojo")
public class EventStats_db extends Model {


    @Column(name = "attendingCount")
    private Integer attendingCount;
    @Column(name = "declinedCount")
    private Integer declinedCount;
    @Column(name = "maybeCount")
    private Integer maybeCount;
    @Column(name = "noreplyCount")
    private Integer noreplyCount;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public EventStats_db() {
    }

    public EventStats_db(Integer attendingCount, Integer declinedCount, Integer maybeCount, Integer noreplyCount, Map<String, Object> additionalProperties) {

        this.attendingCount = attendingCount;
        this.declinedCount = declinedCount;
        this.maybeCount = maybeCount;
        this.noreplyCount = noreplyCount;
        this.additionalProperties = additionalProperties;
    }

   /* public List<Event_db> events() {
        return getMany(Event_db.class, "EventStats");
    }*/
    /**
     *
     * @return
     *     The attendingCount
     */
    public Integer getAttendingCount() {
        return attendingCount;
    }

    /**
     *
     * @param attendingCount
     *     The attendingCount
     */
    public void setAttendingCount(Integer attendingCount) {
        this.attendingCount = attendingCount;
    }

    /**
     *
     * @return
     *     The declinedCount
     */
    public Integer getDeclinedCount() {
        return declinedCount;
    }

    /**
     *
     * @param declinedCount
     *     The declinedCount
     */
    public void setDeclinedCount(Integer declinedCount) {
        this.declinedCount = declinedCount;
    }

    /**
     *
     * @return
     *     The maybeCount
     */
    public Integer getMaybeCount() {
        return maybeCount;
    }

    /**
     *
     * @param maybeCount
     *     The maybeCount
     */
    public void setMaybeCount(Integer maybeCount) {
        this.maybeCount = maybeCount;
    }

    /**
     *
     * @return
     *     The noreplyCount
     */
    public Integer getNoreplyCount() {
        return noreplyCount;
    }

    /**
     *
     * @param noreplyCount
     *     The noreplyCount
     */
    public void setNoreplyCount(Integer noreplyCount) {
        this.noreplyCount = noreplyCount;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }


}
