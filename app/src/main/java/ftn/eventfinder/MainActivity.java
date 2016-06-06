package ftn.eventfinder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import ftn.eventfinder.entities.EventStats_db;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.VenueLocation_db;
import ftn.eventfinder.fragments.EventsListFragment;
import ftn.eventfinder.sync.SyncReceiver;
import ftn.eventfinder.sync.SyncService;
import ftn.eventfinder.activities.MyPreferenceActivity;
import ftn.eventfinder.fragments.MyMapFragment;
import ftn.eventfinder.tools.ConnectivityTools;
import ftn.eventfinder.tools.FragmentTransition;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //sync
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    private SyncReceiver sync;
    public static String SYNC_DATA = "SYNC_DATA";
    private String synctime;
    private boolean allowSync;
    private String lookupRadius;

    private boolean allowReviewNotif;
    private boolean allowCommentedNotif;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //defaultView
        //navigationView.getMenu().performIdentifierAction(R.id.nav_map, 0);
       // navigationView.setCheckedItem(R.id.nav_map);
        setUpReceiver();
    }

    private void setUpReceiver(){
        sync = new SyncReceiver();

        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, SyncService.class);
        pendingIntent = PendingIntent.getService(this, 0, alarmIntent, 0);

        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        consultPreferences();
    }

    private void consultPreferences(){
        synctime = sharedPreferences.getString(getString(R.string.pref_sync_list), "1");// pola minuta
        allowSync = sharedPreferences.getBoolean(getString(R.string.pref_sync), false);

        lookupRadius = sharedPreferences.getString(getString(R.string.pref_radius), "1");//1km

        allowCommentedNotif = sharedPreferences.getBoolean(getString(R.string.notif_on_my_comment_key), false);
        allowReviewNotif = sharedPreferences.getBoolean(getString(R.string.notif_on_my_review_key), false);

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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            // Handle the map action
            FragmentTransition.to(MyMapFragment.newInstance(), this, true);
        } else if (id == R.id.nav_list) {
            FragmentTransition.to(EventsListFragment.newInstance(), this, true);
           /* FragmentManager fm = getFragmentManager();

            if (fm.findFragmentById(android.R.id.content) == null) {
                EventsListFragment list = new EventsListFragment();
                fm.beginTransaction().add(android.R.id.content, list).commit();
            }*/

        } else if (id == R.id.nav_refresh) {
            startService(new Intent(this, SyncService.class));
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

    }

}
