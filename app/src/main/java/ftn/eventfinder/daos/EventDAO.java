package ftn.eventfinder.daos;

import android.content.Context;

import ftn.eventfinder.database.DatabaseHandler;
import ftn.eventfinder.database.DbDAO;

/**
 * Created by Jovan on 12.6.2016.
 */
public class EventDAO extends DbDAO {

    private static final String WHERE_ID_EQUALS = DatabaseHandler.ID_COLUMN
            + " =?";

    public EventDAO(Context context) {
        super(context);
    }

}
