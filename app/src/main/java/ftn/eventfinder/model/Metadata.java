
package ftn.eventfinder.model;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Metadata {

    private Integer venues;
    private Integer venuesWithEvents;
    private Integer events;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * 
     * @return
     *     The venues
     */
    public Integer getVenues() {
        return venues;
    }

    /**
     * 
     * @param venues
     *     The venues
     */
    public void setVenues(Integer venues) {
        this.venues = venues;
    }

    /**
     * 
     * @return
     *     The venuesWithEvents
     */
    public Integer getVenuesWithEvents() {
        return venuesWithEvents;
    }

    /**
     * 
     * @param venuesWithEvents
     *     The venuesWithEvents
     */
    public void setVenuesWithEvents(Integer venuesWithEvents) {
        this.venuesWithEvents = venuesWithEvents;
    }

    /**
     * 
     * @return
     *     The events
     */
    public Integer getEvents() {
        return events;
    }

    /**
     * 
     * @param events
     *     The events
     */
    public void setEvents(Integer events) {
        this.events = events;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
