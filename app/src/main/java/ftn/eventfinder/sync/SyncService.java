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

import java.util.List;

import ftn.eventfinder.MainActivity;
import ftn.eventfinder.RetrofitInt.EventsInterface;
import ftn.eventfinder.model.Event;
import ftn.eventfinder.model.EventsResponse;
import ftn.eventfinder.tools.ConnectivityTools;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SyncService extends Service {

    public static String RESULT_CODE = "RESULT_CODE";
    public static String SERVER_RESPONSE = "SERVER_RESPONSE";
    private LocationManager locationManager;
    private String provider;
    private int serverResponse=2;
    Intent ints;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        ints = new Intent(MainActivity.SYNC_DATA);
        int status = ConnectivityTools.getConnectivityStatus(getApplicationContext());

        ints.putExtra(RESULT_CODE, status);

        //ima konekcije ka netu skini sta je potrebno i sinhronizuj bazu
        if(status == ConnectivityTools.TYPE_WIFI || status == ConnectivityTools.TYPE_MOBILE){
            SyncFromServer();

        }else{
            sendBroadcast(ints);
        }




        stopSelf();
        Log.i("poruka","StopSelf()");

        //sendBroadcast(ints);


        return START_NOT_STICKY;
    }

    private void  SyncFromServer(){
        //sinhronizacija
        locationManager = (LocationManager) getSystemService(
                Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, true);
        Location location=null;
        try {
        location = locationManager.getLastKnownLocation(provider);
        } catch (SecurityException e) {
        }
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
                    for (Event e : events) {
                        //pisanje u bazu .save.
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









}
