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
import com.activeandroid.query.Select;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Random;

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





        Location location = (Location)intent.getExtras().get("location");
        if(location==null){
            Log.i("poruka","location null");
            ints.putExtra(RESULT_CODE, 3);
            ints.putExtra(SERVER_RESPONSE, serverResponse);
            ints.putExtra("LOCATION", 0);
            sendBroadcast(ints);
        }else {
            //ima konekcije ka netu skini sta je potrebno i sinhronizuj bazu
            if (status == ConnectivityTools.TYPE_WIFI || status == ConnectivityTools.TYPE_MOBILE) {
                //SyncFromServer();
                try {
                    LatLng l=new LatLng(location.getLatitude(), location.getLongitude());
                    new SyncTask(getApplicationContext()).execute(l);
                    LatLng l1=getLocation(location.getLatitude(), location.getLongitude(),500);
                    new SyncTask(getApplicationContext()).execute(l1);
                   /* LatLng l2=getLocation(location.getLatitude(), location.getLongitude(),500);
                    new SyncTask(getApplicationContext()).execute(l2);
                    LatLng l3=getLocation(location.getLatitude(), location.getLongitude(),500);
                    new SyncTask(getApplicationContext()).execute(l3);*/


                } catch (Exception e) {
                    //Log.e("SYNC","SyncService", e);
                }

            } else {
                ints.putExtra("LOCATION", 1);
                ints.putExtra(RESULT_CODE, status);
                ints.putExtra(SERVER_RESPONSE, serverResponse);
                sendBroadcast(ints);

            }
        }
        stopSelf();
        Log.i("poruka","StopSelf()");

        //sendBroadcast(ints);


        return START_NOT_STICKY;
    }


    public static LatLng getLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(y0);

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;

        return new LatLng(foundLongitude, foundLatitude);
    }




    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }




}