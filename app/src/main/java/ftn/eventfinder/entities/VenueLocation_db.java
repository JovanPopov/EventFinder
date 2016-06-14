
package ftn.eventfinder.entities;



import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;

@Table(name = "VenueLocations")
@Generated("org.jsonschema2pojo")
public class VenueLocation_db extends Model {

    @Column(name = "venueId")
    private String venueId;
    @Column(name = "venueName")
    private String venueName;
    @Column(name = "venueCoverPicture")
    private String venueCoverPicture;
    @Column(name = "venueProfilePicture")
    private String venueProfilePicture;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;
    @Column(name = "latitude")
    private Double latitude;
    @Column(name = "longitude")
    private Double longitude;
    @Column(name = "state")
    private String state;
    @Column(name = "street")
    private String street;
    @Column(name = "zip")
    private String zip;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public VenueLocation_db() {
    }

    public VenueLocation_db(String venueId, String venueName, String venueCoverPicture, String venueProfilePicture, String city, String country, Double latitude, Double longitude, String state, String street, String zip, Map<String, Object> additionalProperties) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.venueCoverPicture = venueCoverPicture;
        this.venueProfilePicture = venueProfilePicture;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
        this.street = street;
        this.zip = zip;
        this.additionalProperties = additionalProperties;
    }

    public String getVenueId() {
        return venueId;
    }

    public void setVenueId(String venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getVenueCoverPicture() {
        return venueCoverPicture;
    }

    public void setVenueCoverPicture(String venueCoverPicture) {
        this.venueCoverPicture = venueCoverPicture;
    }

    public String getVenueProfilePicture() {
        return venueProfilePicture;
    }

    public void setVenueProfilePicture(String venueProfilePicture) {
        this.venueProfilePicture = venueProfilePicture;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public List<Event_db> events() {
        return getMany(Event_db.class, "VenueLocation");
    }
}
