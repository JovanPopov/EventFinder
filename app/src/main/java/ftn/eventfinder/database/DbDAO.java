package ftn.eventfinder.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import ftn.eventfinder.database.DatabaseHandler;

/**
 * Created by Jovan on 12.6.2016.
 */
public class DbDAO {
    protected SQLiteDatabase database;
    private DatabaseHandler dbHelper;
    private Context mContext;

    public DbDAO(Context context) {
        this.mContext = context;
        dbHelper = DatabaseHandler.getHelper(mContext);
        open();

    }

    public void open() throws SQLException {
        if(dbHelper == null)
            dbHelper = DatabaseHandler.getHelper(mContext);
        database = dbHelper.getWritableDatabase();
    }

    /*public void close() {
        dbHelper.close();
        database = null;
    }*/
}
