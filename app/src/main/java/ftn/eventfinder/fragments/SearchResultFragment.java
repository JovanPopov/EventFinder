package ftn.eventfinder.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.activities.EventDetail;
import ftn.eventfinder.adapters.EventListAdapter;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.Tag_db;
import ftn.eventfinder.entities.VenueLocation_db;

/**
 * Created by Jovan on 4.6.2016.
 */
public class SearchResultFragment extends ListFragment {
    private String query;

    EventListAdapter mAdapter;

    public static SearchResultFragment newInstance() {

        SearchResultFragment mpf = new SearchResultFragment();


        return mpf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            query = bundle.getString("query");
        }

        super.onCreate(savedInstanceState);
        mAdapter = new EventListAdapter(getActivity(),getResults(query));
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
        //Event_db event = this.queryResults.get(position);
        Event_db event = (Event_db) mAdapter.getItem(position);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        // Boolean splash = ;


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
        //localReceiver

        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mReceiver, new IntentFilter("syncResponse"));
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.clear();
            mAdapter.addAll(getResults(query));
            mAdapter.notifyDataSetChanged();
        }
    };

    public List<Event_db> getResults(String query){

       List<Event_db> start = new Select().from(Event_db.class).orderBy("eventStarttime ASC").execute();
        List<Event_db> result=new ArrayList<Event_db>();
        for (Event_db event:start) {
            if (event.getEventName().toLowerCase().contains(query)) {
                result.add(event);
            }
            for(Tag_db tag:event.getTags()){
                    if(tag.getValue().toLowerCase().contains(query))
                        if(!result.contains(event)) result.add(event);
                   // break;

                }




        }

        List<VenueLocation_db> venues = new Select().from(VenueLocation_db.class).execute();
        for(VenueLocation_db venue:venues) {
            if (venue.getVenueName().toLowerCase().contains(query)) {
                for (Event_db e : venue.events()) {
                    if(!result.contains(e)) result.add(e);


                }
            }
        }

        Log.i("search",query);
        Log.i("search",String.valueOf(result.size()));

        return result;
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        LocalBroadcastManager.getInstance(this.getActivity()).unregisterReceiver(mReceiver);
    }

}
