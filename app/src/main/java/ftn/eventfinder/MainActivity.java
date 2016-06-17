package ftn.eventfinder;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.List;

import ftn.eventfinder.dialogs.LocationDialog;
import ftn.eventfinder.dialogs.SortDialog;
import ftn.eventfinder.entities.EventStats_db;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.VenueLocation_db;
import ftn.eventfinder.fragments.EventsListFragment;
import ftn.eventfinder.fragments.VenuesListFragment;
import ftn.eventfinder.sync.SyncReceiver;
import ftn.eventfinder.sync.SyncService;
import ftn.eventfinder.activities.MyPreferenceActivity;
import ftn.eventfinder.fragments.MyMapFragment;
import ftn.eventfinder.tools.ConnectivityTools;
import ftn.eventfinder.tools.FragmentTransition;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, ResultCallback<LocationSettingsResult> {

    //sync
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    private SyncReceiver sync;
    public static String SYNC_DATA = "SYNC_DATA";
    private String synctime;
    private boolean allowSync;
    private String lookupRadius;

    private android.support.v7.app.AlertDialog dialog;

    private boolean allowReviewNotif;
    private boolean allowCommentedNotif;
    private SharedPreferences sharedPreferences;

    //fusedLocation
    Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private boolean locationChecked;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // Toast.makeText(MainActivity.this,String.valueOf(savedInstanceState.get("deleted")) , Toast.LENGTH_SHORT).show();
        Log.i("main", "onCreate()");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String home = sharedPreferences.getString("pref_home_list", "debug");
        if(savedInstanceState==null) {
            Log.i("main", "saved state null");
            //defaultView

            if (home.equals("map")) {
                navigationView.getMenu().performIdentifierAction(R.id.nav_map, 0);
                navigationView.setCheckedItem(R.id.nav_map);
            } else if (home.equals("list")) {
                navigationView.getMenu().performIdentifierAction(R.id.nav_list, 0);
                navigationView.setCheckedItem(R.id.nav_list);
            }


        }else{
            Log.i("main", "Saved state not null");
            locationChecked=savedInstanceState.getBoolean("locationChecked");
            String fragmentTag=home;
            if(savedInstanceState.getBoolean("map")){
                fragmentTag="map";
            }else if(savedInstanceState.getBoolean("list")){
                fragmentTag="list";
            }else if(savedInstanceState.getBoolean("venue")){
            fragmentTag="venue";
        }
            Log.i("main", fragmentTag);
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment existingFragment = fm.findFragmentByTag(fragmentTag);
            FragmentTransition.to(existingFragment, this, fragmentTag);
        }

        setUpReceiver();
        buildGoogleApiClient();

    /*    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getSupportActionBar().hide();
        }
        else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }*/

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("main", "onSaveInstanceState()");
        FragmentManager fm = this.getSupportFragmentManager();
        MyMapFragment myFragment = (MyMapFragment) fm.findFragmentByTag("map");
        if (myFragment != null && myFragment.isVisible()) {
            outState.putBoolean("map", true);
            Log.i("main", "map saved )");
        }

        EventsListFragment myFragment1 = (EventsListFragment)fm.findFragmentByTag("list");
        if (myFragment1 != null && myFragment1.isVisible()) {
            outState.putBoolean("list", true);
            Log.i("main", "list saved ");
        }

        VenuesListFragment myFragment2 = (VenuesListFragment)fm.findFragmentByTag("venue");
        if (myFragment2 != null && myFragment2.isVisible()) {
            outState.putBoolean("venue", true);
            Log.i("main", "venue saved ");
        }
        outState.putBoolean("locationChecked", locationChecked);
    }

    private void setUpReceiver(){
        sync = new SyncReceiver();

        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, SyncService.class);
        pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);



        consultPreferences();
    }

    private void consultPreferences(){
        synctime = sharedPreferences.getString(getString(R.string.pref_sync_list), "1");// pola minuta
        allowSync = sharedPreferences.getBoolean(getString(R.string.pref_sync), false);

        //lookupRadius = sharedPreferences.getString(getString(R.string.pref_radius), "1");//1km

        //allowCommentedNotif = sharedPreferences.getBoolean(getString(R.string.notif_on_my_comment_key), false);
        //allowReviewNotif = sharedPreferences.getBoolean(getString(R.string.notif_on_my_review_key), false);

        //Toast.makeText(MainActivity.this, allowSync + " " + lookupRadius + " " + synctime, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_sort) {
            if (dialog == null) {
                dialog = new SortDialog(this).prepareDialog();
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
            }

            dialog.show();
            return true;
        }

        if (id == R.id.action_refresh) {
            startService(getLocation());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            // Handle the map action
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment existingFragment = fm.findFragmentByTag("map");
            if(existingFragment==null) {
                MyMapFragment toFragment = MyMapFragment.newInstance();
                if (mLastLocation != null) {
                    Bundle args = new Bundle();
                    args.putDouble("lat", mLastLocation.getLatitude());
                    args.putDouble("lng", mLastLocation.getLongitude());
                    toFragment.setArguments(args);
                }
                FragmentTransition.to(toFragment, this, "map");
            }else{
                FragmentTransition.to(existingFragment, this, "map");
            }




        } else if (id == R.id.nav_list) {
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment existingFragment = fm.findFragmentByTag("list");
            if(existingFragment==null) {
                FragmentTransition.to(EventsListFragment.newInstance(), this, "list");
            }else{
                FragmentTransition.to(existingFragment, this, "list");
            }
           /* FragmentManager fm = getFragmentManager();

            if (fm.findFragmentById(android.R.id.content) == null) {
                EventsListFragment list = new EventsListFragment();
                fm.beginTransaction().add(android.R.id.content, list).commit();
            }*/

        } else if (id == R.id.nav_venue) {
            FragmentManager fm = this.getSupportFragmentManager();
            Fragment existingFragment = fm.findFragmentByTag("venue");
            if(existingFragment==null) {
                FragmentTransition.to(VenuesListFragment.newInstance(), this, "venue");
            }else{
                FragmentTransition.to(existingFragment, this, "venue");
            }
           /* FragmentManager fm = getFragmentManager();

            if (fm.findFragmentById(android.R.id.content) == null) {
                EventsListFragment list = new EventsListFragment();
                fm.beginTransaction().add(android.R.id.content, list).commit();
            }*/

        }




        else if (id == R.id.nav_refresh) {
            startService(getLocation());


        } else if (id == R.id.nav_settings) {
            Intent preference = new Intent(MainActivity.this,MyPreferenceActivity.class);
            startActivity(preference);
        } else if (id == R.id.nav_share) {
            new Delete().from(EventStats_db.class).execute();
            new Delete().from(VenueLocation_db.class).execute();
            new Delete().from(Event_db.class).execute();
            List<Event_db> eve=new Select().from(Event_db.class).execute();
            List<EventStats_db> eve1=new Select().from(EventStats_db.class).execute();
            List<VenueLocation_db> eve2=new Select().from(VenueLocation_db.class).execute();
            Toast.makeText(this,"Events size: " + String.valueOf(eve.size()) + "\n" +
                            "VenueLocation size: " + String.valueOf(eve2.size())+ "\n" +
                            "EventStats size: " + String.valueOf(eve1.size())
                    , Toast.LENGTH_LONG).show();
        } else if (id == R.id.nav_send) {
            int eve=new Select().from(Event_db.class).count();
            int eve1=new Select().from(EventStats_db.class).count();
            int eve2=new Select().from(VenueLocation_db.class).count();
            Toast.makeText(this,"Events size: " + String.valueOf(eve) + "\n" +
                    "VenueLocation size: " + String.valueOf(eve2)+ "\n" +
                    "EventStats size: " + String.valueOf(eve1)
                    , Toast.LENGTH_LONG).show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        //Za slucaj da referenca nije postavljena da se izbegne problem sa androidom!
        if (manager == null) {
            setUpReceiver();
        }

        if(allowSync){
            int interval = ConnectivityTools.calculateTimeTillNextSync(Integer.parseInt(synctime));
            manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(SYNC_DATA);

        registerReceiver(sync, filter);
        mGoogleApiClient.connect();

    }
    @Override
    protected void onPause() {
        if (manager != null) {
            manager.cancel(pendingIntent);
        }

        //osloboditi resurse
        if(sync != null){
            unregisterReceiver(sync);
        }

        super.onPause();
         mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        String synct = sharedPreferences.getString(getString(R.string.pref_sync_list), "10000");
        int time=Integer.parseInt(synct);
        Toast.makeText(this, "onConnected()", Toast.LENGTH_SHORT).show();
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(time); // Update location every second 10000
        mLocationRequest.setFastestInterval(time);
        //mLocationRequest.setSmallestDisplacement(15);
        try{
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
           // Log.i("GlobalLocVar", String.valueOf(mLastLocation));
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "getLocation error", Toast.LENGTH_SHORT).show();
         }


        if(!locationChecked) {
            checkLocation();
            locationChecked=true;
        }

    }


    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        final LocationSettingsStates LS_state = locationSettingsResult.getLocationSettingsStates();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // All location settings are satisfied. The client can initialize location
                // requests here.

                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                // Location settings are not satisfied. But could be fixed by showing the user
                // a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    status.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {
                    // Ignore the error.
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are not satisfied. However, we have no way to fix the
                // settings so we won't show the dialog.

                break;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "onConnectionSuspended()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        buildGoogleApiClient();
        Toast.makeText(this, "onConnectionFailed()", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location updated", Toast.LENGTH_SHORT).show();
        mLastLocation=location;
        startService(location);

        if (ConnectivityTools.getConnectivityStatus(getApplicationContext()) == ConnectivityTools.TYPE_NOT_CONNECTED) {
            Intent intent = new Intent("syncResponse");
            intent.putExtra("lat",location.getLatitude());
            intent.putExtra("lng",location.getLongitude());
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }
    private void checkLocation(){
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(this);
    }

    private void startService(Location location){
        if(location==null){
            checkLocation();
        }else {

            if (ConnectivityTools.getConnectivityStatus(getApplicationContext()) != ConnectivityTools.TYPE_NOT_CONNECTED) {
                Intent si = new Intent(this, SyncService.class);
                si.putExtra("location", location);
                startService(si);
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Location getLocation(){
        Location loc=null;
        try{
            loc = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "getLocation error", Toast.LENGTH_SHORT).show();
        }
        return loc;
    }

    synchronized void buildGoogleApiClient() {
        Log.i("main", "Build GoogleApiclient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


    }




    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
    private void showLocatonDialog() {
        if (dialog == null) {
            dialog = new LocationDialog(this).prepareDialog();
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }

        dialog.show();
    }

}
