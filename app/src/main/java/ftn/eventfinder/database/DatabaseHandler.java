package ftn.eventfinder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jovan on 12.6.2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "EventFinderDb";
    private static final int DATABASE_VERSION = 1;

    public static final String EVENT_TABLE = "Event";
    public static final String VENUELOCATION_TABLE = "VenueLocation";
    public static final String EVENTSTATS_TABLE = "EventStats";


    public static final String ID_COLUMN = "id";

    //FOR EVENTS TABLE

    public static final String venueId="venueId";
    public static final String venueName="venueName";
    public static final String venueCoverPicture="venueCoverPicture";
    public static final String venueProfilePicture="venueProfilePicture";
    public static final String venueLocation_id="venueLocation_id";
    public static final String eventId="eventId";
    public static final String eventName="eventName";
    public static final String eventCoverPicture="eventCoverPicture";
    public static final String eventProfilePicture="eventProfilePicture";
    public static final String eventDescription="eventDescription";
    public static final String eventStarttime="eventStarttime";
    public static final String eventDistance="eventDistance";
    public static final String eventTimeFromNow="eventTimeFromNow";
    public static final String eventStats_id="eventStats_id";


    //FOR VENUELOCATION TABLE

    public static final String city="city";
    public static final String country="country";
    public static final String latitude="latitude";
    public static final String longitude="longitude";
    public static final String state="state";
    public static final String street="street";
    public static final String zip="zip";


    //FOR EVENT STATS TABLE

    public static final String attendingCount="attendingCount";
    public static final String declinedCount="declinedCount";
    public static final String maybeCount="maybeCount";
    public static final String noreplyCount="noreplyCount";

    public static final String CREATE_EVENT_TABLE = "CREATE TABLE "
            + EVENT_TABLE + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY, "
            + venueId + " TEXT, "
            + venueName + " TEXT, "
            + venueCoverPicture + " TEXT, "
            + venueProfilePicture + " TEXT, "
            + venueLocation_id + " INT, "
            + eventId + " TEXT, "
            + eventName + " TEXT, "
            + eventCoverPicture + " TEXT, "
            + eventProfilePicture + " TEXT, "
            + eventDescription + " TEXT, "
            + eventStarttime + " TEXT, "
            + eventDistance + " TEXT, "
            + eventTimeFromNow + " INT, "
            + eventStats_id + " INT, "

            + "FOREIGN KEY(" + venueLocation_id + ") REFERENCES "
            + VENUELOCATION_TABLE + "(id) "
            + "FOREIGN KEY(" + eventStats_id + ") REFERENCES "
            + EVENTSTATS_TABLE + "(id) "

            + ")";

    public static final String CREATE_EVENTSTATS_TABLE = "CREATE TABLE "
            + EVENTSTATS_TABLE + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY,"
            + city + " TEXT, "
            + country + " TEXT, "
            + latitude + " DOUBLE, "
            + longitude + " DOUBLE, "
            + state + " TEXT, "
            + street + " TEXT, "
            + zip + " TEXT, "

            + ")";

    public static final String CREATE_VENUELOCATION_TABLE = "CREATE TABLE "
            + VENUELOCATION_TABLE + "("
            + ID_COLUMN + " INTEGER PRIMARY KEY,"
            + attendingCount + " INT, "
            + declinedCount + " INT, "
            + maybeCount + " INT, "
            + noreplyCount + " INT, "

            + ")";

    private static DatabaseHandler instance;

    public static synchronized DatabaseHandler getHelper(Context context) {
        if (instance == null)
            instance = new DatabaseHandler(context);
        return instance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_VENUELOCATION_TABLE);
        db.execSQL(CREATE_EVENT_TABLE);
        db.execSQL(CREATE_EVENTSTATS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}
