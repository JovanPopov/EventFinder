package ftn.eventfinder.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
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
import ftn.eventfinder.adapters.EventListAdapter;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.model.Event;

/**
 * Created by Jovan on 4.6.2016.
 */
public class EventsListFragment extends ListFragment {

    List<Event_db> queryResults= new ArrayList<Event_db>();;
    EventListAdapter mAdapter;

    public static EventsListFragment newInstance() {

        EventsListFragment mpf = new EventsListFragment();


        return mpf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryResults=new Select().from(Event_db.class).execute();
        mAdapter = new EventListAdapter(getActivity(), new Select().from(Event_db.class).execute());
        setListAdapter(mAdapter);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view_fragment, container, false);



        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Log.i("poruka","usao u listener");
        Event_db event = this.queryResults.get(position);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/events/"+event.getEventId()));
        startActivity(browserIntent);

    }
    @Override
    public void onResume() {
        super.onResume();

        //navigation drawer highlighting
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_list);
        //localReceiver
        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mReceiver, new IntentFilter("syncResponse"));
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.clear();
            mAdapter.addAll(new Select().from(Event_db.class).execute());
            mAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mReceiver);
    }

}
