package ftn.eventfinder.sync;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import ftn.eventfinder.MainActivity;
import ftn.eventfinder.RetrofitInt.EventsInterface;
import ftn.eventfinder.entities.EventStats_db;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.VenueLocation_db;
import ftn.eventfinder.model.Event;
import ftn.eventfinder.model.EventStats;
import ftn.eventfinder.model.EventsResponse;
import ftn.eventfinder.model.VenueLocation;
import ftn.eventfinder.tools.ConnectivityTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SyncService extends Service {

    public static String RESULT_CODE = "RESULT_CODE";
    public static String SERVER_RESPONSE = "SERVER_RESPONSE";
    private int serverResponse = 2;
    Intent ints;
    Location location=null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ints = new Intent(MainActivity.SYNC_DATA);
        int status = ConnectivityTools.getConnectivityStatus(getApplicationContext());

        ints.putExtra(RESULT_CODE, status);

        /*locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);




        LatLng loc=null;
        try {
            location = locationManager.getLastKnownLocation(provider);




        } catch (SecurityException e) {
        }
*/
    Location location = (Location)intent.getExtras().get("location");
        //ima konekcije ka netu skini sta je potrebno i sinhronizuj bazu
        if (status == ConnectivityTools.TYPE_WIFI || status == ConnectivityTools.TYPE_MOBILE) {
            //SyncFromServer();
            try {
                new SyncTask(getApplicationContext()).execute(new LatLng(location.getLatitude(), location.getLongitude()));
            }catch(Exception e){
                //Log.e("SYNC","SyncService", e);
            }

        } else{


            sendBroadcast(ints);

}

        stopSelf();
        Log.i("poruka","StopSelf()");

        //sendBroadcast(ints);


        return START_NOT_STICKY;
    }





    private void  SyncFromServer(){
        //sinhronizacija

        if(location!=null) {
            final String BASE_URL = "http://188.2.87.248:3000/";

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();


            EventsInterface service = retrofit.create(EventsInterface.class);
            Call<EventsResponse> call = service.getEvents(location.getLatitude(), location.getLongitude());
            call.enqueue(new Callback<EventsResponse>() {
                @Override
                public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                    int statusCode = response.code();
                    EventsResponse eventsResponse = response.body();
                    Log.i("poruka", response.message());
                    serverResponse = ConnectivityTools.SERVER_RESPONSE_OK;
                    

                    List<Event> events = eventsResponse.getEvents();


                    clearDb();
                    for (Event e : events) {
                        //pisanje u bazu
                        saveEntity(e);
                    }
                    //PopulateWithMarkers();


                    ints.putExtra(SERVER_RESPONSE, serverResponse);
                    sendBroadcast(ints);

                }

                @Override
                public void onFailure(Call<EventsResponse> call, Throwable t) {
                    // Log error here since request failed
                    Log.i("odgovor", t.getMessage());

                    serverResponse = ConnectivityTools.SERVER_RESPONSE_ERROR;
                    ints.putExtra(SERVER_RESPONSE, serverResponse);
                    sendBroadcast(ints);
                }
            });

        }else{
            Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

private void saveEntity(Event e){
    Log.i("odgovor", "saveEntity()");





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
}