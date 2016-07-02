package ftn.eventfinder.entities;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by Jovan on 2.7.2016.
 */
@Table(name = "Tags")
public class Tag_db extends Model {

    @Column(name = "tagId")
    int tagId;
    @Column(name = "value")
    String value;
    @Column(name = "weight")
    int weight;
    @Column(name = "event", onUpdate = Column.ForeignKeyAction.CASCADE, onDelete = Column.ForeignKeyAction.CASCADE)
    Event_db event_db;
    @Column(name = "vote")
    boolean vote;

    public Tag_db() {
    }

    public Tag_db(String value, int weight, Event_db event_db, int tagId, boolean vote) {
        this.value = value;
        this.weight = weight;
        this.event_db = event_db;
        this.tagId=tagId;
        this.vote=vote;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Event_db getEvent_db() {
        return event_db;
    }

    public void setEvent_db(Event_db event_db) {
        this.event_db = event_db;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public boolean isVote() {
        return vote;
    }

    public void setVote(boolean vote) {
        this.vote = vote;
    }
}
