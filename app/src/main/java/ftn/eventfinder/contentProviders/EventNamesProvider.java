package ftn.eventfinder.contentProviders;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import com.activeandroid.content.ContentProvider;
import com.activeandroid.query.Select;

import java.util.List;

import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.Tag_db;
import ftn.eventfinder.entities.VenueLocation_db;

/**
 * Created by Jovan on 1.7.2016.
 */
public class EventNamesProvider extends ContentProvider {

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder, CancellationSignal cancellationSignal) {



        //return super.query(uri, projection, selection, selectionArgs, sortOrder, cancellationSignal);

        List<Event_db> events= new Select().from(Event_db.class).execute();


        MatrixCursor cursor = new MatrixCursor(
                new String[] {
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                }
        );
        String query = uri.getLastPathSegment().toUpperCase();
        int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));
        if (events != null) {


            int lenght = events.size();
            for (int i = 0; i < lenght && cursor.getCount() < limit; i++) {
                Event_db event = events.get(i);




                if (event.getEventName().toUpperCase().contains(query)){
                    cursor.addRow(new Object[]{ i, event.getEventName(), event.getEventId() });
                }
                    for(Tag_db tag:event.getTags()){
                        if(tag.getValue().toUpperCase().contains(query))
                            cursor.addRow(new Object[]{ i, "#" + tag.getValue().toUpperCase() + " " + event.getEventName(), event.getEventId() });
                            break;
                    }



            }
        }

        List<VenueLocation_db> venues= new Select().from(VenueLocation_db.class).execute();
        int lenght = venues.size();
        for (int i = 0; i < lenght && cursor.getCount() < limit; i++){
            VenueLocation_db venue = venues.get(i);
            if (venue.getVenueName().toUpperCase().contains(query)) {
                for (Event_db event : venue.events()) {
                    cursor.addRow(new Object[]{i, "venue: " + venue.getVenueName() + ", " + event.getEventName(), event.getEventId()});


                }
            }
        }
        return cursor;


        //return Event_db.fetchResultCursor();
    }
}
