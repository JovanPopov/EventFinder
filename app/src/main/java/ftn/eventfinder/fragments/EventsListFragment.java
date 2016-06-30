package ftn.eventfinder.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.activities.EventDetail;
import ftn.eventfinder.adapters.EventListAdapter;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.model.Event;

/**
 * Created by Jovan on 4.6.2016.
 */
public class EventsListFragment extends ListFragment {


    EventListAdapter mAdapter;
    boolean favourite;
    MenuItem item;

    public static EventsListFragment newInstance() {

        EventsListFragment mpf = new EventsListFragment();


        return mpf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            favourite=false;
        }else{
            favourite=savedInstanceState.getBoolean("favourite");
        }
            if(!favourite) {
                mAdapter = new EventListAdapter(getActivity(), new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute());
            }else{
                List<Event_db> list = new Select().from(Event_db.class).orderBy("eventStarttime ASC").where("favourite = ?", true).execute();
                if (!list.isEmpty()) {
                    mAdapter = new EventListAdapter(getActivity(), list);
                }else{
                    mAdapter = new EventListAdapter(getActivity(), new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute());
                    favourite=false;
                }


            }
        setListAdapter(mAdapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_fragment, container, false);

        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("favourite", favourite);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Event_db event = this.queryResults.get(position);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        // Boolean splash = ;

        Event_db event = (Event_db) mAdapter.getItem(position);

        if(sharedPreferences.getBoolean("pref_face", false)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/events/" + event.getEventId()));
            startActivity(browserIntent);
        }else{
            Intent intent = new Intent(getActivity(), EventDetail.class);
            intent.putExtra("id", event.getEventId());
            startActivity(intent);
        }


    }
    @Override
    public void onResume() {
        super.onResume();

        //navigation drawer highlighting
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_list);
        //localReceiver

        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mReceiver, new IntentFilter("syncResponse"));

        if(favourite) {
            List<Event_db> list = new Select().from(Event_db.class).orderBy("eventStarttime ASC").where("favourite = ?", true).execute();
            if (!list.isEmpty()) {

                mAdapter.clear();
                mAdapter.addAll(list);
                mAdapter.notifyDataSetChanged();

            } else {
                mAdapter.clear();
                mAdapter.addAll(new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute());
                mAdapter.notifyDataSetChanged();
                favourite=false;
            }
        }else{
            mAdapter.clear();
            mAdapter.addAll(new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute());
            mAdapter.notifyDataSetChanged();
        }

        if(item!=null) {
            if (favourite) {
                item.setIcon(R.drawable.ic_favorite_white_24dp);
            } else {
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
            }
        }
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(!favourite) {
                mAdapter.clear();
                mAdapter.addAll(new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute());
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mReceiver);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.events_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        item = menu.findItem(R.id.event_toolbar_add);
        if(favourite)
        item.setIcon(R.drawable.ic_favorite_white_24dp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.event_toolbar_add) {

            favourite = !favourite;

            if(favourite) {

                List<Event_db> list = new Select().from(Event_db.class).orderBy("eventStarttime ASC").where("favourite = ?", true).execute();
                if (!list.isEmpty()) {
                    mAdapter.clear();
                    mAdapter.addAll(list);
                    mAdapter.notifyDataSetChanged();
                } else {
                    favourite = !favourite;
                    Toast.makeText(getActivity(), "No events favourited", Toast.LENGTH_SHORT).show();
                }

            }else{

                mAdapter.clear();
                mAdapter.addAll(new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute());
                mAdapter.notifyDataSetChanged();
            }

            if(favourite) {
                item.setIcon(R.drawable.ic_favorite_white_24dp);
            }else{
                item.setIcon(R.drawable.ic_favorite_border_white_24dp);
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
