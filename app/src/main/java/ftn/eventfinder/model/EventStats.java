
package ftn.eventfinder.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class EventStats {

    private Integer attendingCount;
    private Integer declinedCount;
    private Integer maybeCount;
    private Integer noreplyCount;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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
