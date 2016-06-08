package ftn.eventfinder.sync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import ftn.eventfinder.MainActivity;
import ftn.eventfinder.R;
import ftn.eventfinder.RetrofitInt.EventsInterface;
import ftn.eventfinder.entities.EventStats_db;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.VenueLocation_db;
import ftn.eventfinder.model.Event;
import ftn.eventfinder.model.EventStats;
import ftn.eventfinder.model.EventsResponse;
import ftn.eventfinder.model.VenueLocation;
import ftn.eventfinder.tools.ConnectivityTools;
import ftn.eventfinder.tools.SyncUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SyncTask extends AsyncTask<LatLng, Void, String> {



    private Context context;

    private Date dateOfSynchronization;
    private Date dateLastSynchronized;
    private SimpleDateFormat dateFormat;
    private String dateLastSynchronizedString;

    public static String RESULT_CODE = "RESULT_CODE";

    private LatLng loc=null;
    Intent ints;
    List<Event> events;
    int newEntites=0;
    public static String SERVER_RESPONSE = "SERVER_RESPONSE";




    public SyncTask(Context context) {
        this.context = context;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onPreExecute() {
        dateOfSynchronization = new Date();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

   @Override
    protected String doInBackground(LatLng... params) {

       ints = new Intent(MainActivity.SYNC_DATA);


        dateLastSynchronized = SyncUtils.getLastSyncronizationDate(context);
        if (dateLastSynchronized == null) // ako nije do sad sinhronizovan
        {
            dateLastSynchronized = new Date(0); // sinhronizuj sve od pocetka epohe
        }
        dateLastSynchronizedString = dateFormat.format(dateLastSynchronized);

        loc=params[0];
       Log.i("poruka", "sync pokrenut");
        try {
            download(loc);
            SyncUtils.setLastSyncronizationDate(context, new Date());

        } catch (IOException e) {
            ints.putExtra(SERVER_RESPONSE, ConnectivityTools.SERVER_RESPONSE_ERROR);
            Log.e("SYNC", "SyncTask", e);
            return e.getMessage();
        }
       try {
           persist();
           SyncUtils.setLastSyncronizationDate(context, new Date());
       } catch (Exception e) {
           ints.putExtra(SERVER_RESPONSE, ConnectivityTools.SERVER_RESPONSE_ERROR);
           Log.e("SYNC", "SyncTask", e);
           return e.getMessage();
       }

       ints.putExtra(SERVER_RESPONSE, ConnectivityTools.SERVER_RESPONSE_OK);
        return "Sync sucsessfull";
    }

	/*
	 * DOWNLOAD DATA FROM SERVER
	 */

    private void download(LatLng location) throws IOException {

        if(location!=null) {
            final String BASE_URL = "http://188.2.87.248:3000/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            EventsInterface service = retrofit.create(EventsInterface.class);
            Call<EventsResponse> call = service.getEvents(location.latitude, location.longitude);
            EventsResponse eventResponse=call.execute().body();
            if(call.isExecuted()){
                Log.i("poruka", "call is executed");
            }else{
                Log.i("poruka", "call is not executed");
            }
            events = eventResponse.getEvents();




          /*  call.enqueue(new Callback<EventsResponse>() {


                @Override
                public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                    int statusCode = response.code();
                    EventsResponse eventsResponse = response.body();
                    Log.i("poruka", response.message());


                    List<Event> events = eventsResponse.getEvents();


                    clearDb();
                    for (Event e : events) {
                        //pisanje u bazu
                        saveEntity(e);
                    }
                    //PopulateWithMarkers();




                }

                @Override
                public void onFailure(Call<EventsResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.i("poruka", t.getMessage());

                }
            });*/

        }
    }
    private void saveEntity(Event e){
        Log.i("poruka", "Saving entity");

        VenueLocation vl=e.getVenueLocation();
        VenueLocation_db vldb = new VenueLocation_db();
        vldb.setCity(vl.getCity());
        vldb.setCountry(vl.getCountry());
        vldb.setLatitude(vl.getLatitude());
        vldb.setLongitude(vl.getLongitude());
        vldb.setState(vl.getState());
        vldb.setStreet(vl.getStreet());
        vldb.setZip(vl.getZip());

        vldb.save();

        EventStats es=e.getEventStats();
        EventStats_db esdb = new EventStats_db();
        esdb.setAttendingCount(es.getAttendingCount());
        esdb.setDeclinedCount(es.getDeclinedCount());
        esdb.setMaybeCount(es.getMaybeCount());
        esdb.setNoreplyCount(es.getNoreplyCount());

        esdb.save();


        Event_db edb = new Event_db();
        edb.setEventCoverPicture(e.getEventCoverPicture());
        edb.setEventDescription(e.getEventDescription());
        edb.setEventDistance(e.getEventDistance());
        edb.setEventId(e.getEventId());
        edb.setEventName(e.getEventName());
        edb.setEventProfilePicture(e.getEventProfilePicture());
        edb.setEventStarttime(e.getEventStarttime());
        edb.setEventStats(esdb);
        edb.setEventCoverPicture(e.getEventCoverPicture());
        edb.setEventTimeFromNow(e.getEventTimeFromNow());
        edb.setVenueCoverPicture(e.getVenueCoverPicture());
        edb.setVenueId(e.getVenueId());
        edb.setVenueLocation(vldb);
        edb.setVenueName(e.getVenueName());

        edb.save();



    }

    private void clearDb(){
        new Delete().from(EventStats_db.class).execute();
        new Delete().from(VenueLocation_db.class).execute();
        new Delete().from(Event_db.class).execute();
    }

	private void persist()
	{

        ActiveAndroid.beginTransaction();
        try {
        List<Event_db> queryResults=new Select().from(Event_db.class).execute();
        for (Event e : events) {


           if(containsEvent(queryResults, e.getEventId())) {
               saveEntity(e);
               newEntites++;
           }
        }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }


	}
    public static boolean containsEvent(List<Event_db> ev, String id) {
        for(Event_db o : ev) {
            if(o != null && o.getEventId().equals(id)) {
                return false;
            }
        }
        return true;
    }



    @Override
	protected void onPostExecute(String result) {

        Intent intent = new Intent("syncResponse");
        intent.putExtra("foo", "sync compleated, repopulate map");
        intent.putExtra("lat",loc.latitude);
        intent.putExtra("lng",loc.longitude);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);


   // context.sendBroadcast(ints);

        Toast toast = new Toast(context);
        ImageView view = new ImageView(context);
        view.setImageResource(R.drawable.ic_action_refresh);
        toast.setView(view);
        toast.show();


        //Toast.makeText(context, "New Entites: " + String.valueOf(newEntites), Toast.LENGTH_SHORT).show();
        Log.i("poruka", result);

	}
}
