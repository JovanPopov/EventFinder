package ftn.eventfinder.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ftn.eventfinder.MainActivity;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.VenueLocation_db;

/**
 * Created by Jovan on 6.6.2016.
 */
public class ClearDbService extends IntentService {

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public ClearDbService() {
        super("ClearDbService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Normally we would do some work here, like download a file.
        // For our sample, we just sleep for 5 seconds.
        Log.i("clear", "clearDbService pokrenut");
        try {
            int deletes=0;
            List<Event_db> queryResults=new Select().from(Event_db.class).execute();
            SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
            Date eventDate=null;
            //Date currentDate=new Date();

            int hours = 6;
            Calendar calendar = Calendar.getInstance();
            calendar.set( Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hours );
            Date currentDate = calendar.getTime();

            for (Event_db e : queryResults) {
                try {
                    eventDate = incomingFormat.parse(e.getEventStarttime());
                } catch (ParseException e1) {
                    e1.printStackTrace();
                }

                if(eventDate.before(currentDate)){
                    Log.i("clear", "entity deleted" + String.valueOf(eventDate) + e.getEventName());



                    e.getEventStats().delete();
                    e.delete();


                    deletes++;
                }



            }

            List<VenueLocation_db> queryResultsVenu=new Select().from(VenueLocation_db.class).execute();

            for (VenueLocation_db vn :queryResultsVenu
                 ) {
                if( vn.events().size()==0) {
                    Log.i("clear", "venue deleted" + vn.getVenueName());
                    vn.delete();
                }
            }


            //showToast("Deleted entities: " + String.valueOf(deletes));


           /* Intent dialogIntent = new Intent(this, MainActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            dialogIntent.putExtra("deleted", deletes);
            getApplication().startActivity(dialogIntent);*/


        } catch (Exception e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }

  /*  public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }*/

}

