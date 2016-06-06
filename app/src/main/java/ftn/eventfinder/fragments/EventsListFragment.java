package ftn.eventfinder.fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ListFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    public static EventsListFragment newInstance() {

        EventsListFragment mpf = new EventsListFragment();


        return mpf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryResults=new Select().from(Event_db.class).execute();
        EventListAdapter mAdapter = new EventListAdapter(getActivity(), queryResults);
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

}
