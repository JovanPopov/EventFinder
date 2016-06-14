package ftn.eventfinder.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.activeandroid.query.Select;

import ftn.eventfinder.R;
import ftn.eventfinder.activities.VenueDetail1;
import ftn.eventfinder.adapters.VenueListAdapter;
import ftn.eventfinder.entities.VenueLocation_db;

/**
 * Created by Jovan on 4.6.2016.
 */
public class VenuesListFragment extends ListFragment {


    VenueListAdapter mAdapter;

    public static VenuesListFragment newInstance() {

        VenuesListFragment mpf = new VenuesListFragment();


        return mpf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new VenueListAdapter(getActivity(), new Select().from(VenueLocation_db.class).execute());
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
        //Log.i("poruka","usao u listener");
        VenueLocation_db venue = (VenueLocation_db) mAdapter.getItem(position);
        //Event_db event = this.queryResults.get(position);
        //Event_db event = (Event_db) mAdapter.getItem(position);

        //ovo je za otvaranje linka
       //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+venue.getVenueId()));
       //startActivity(browserIntent);

        Intent intent = new Intent(getActivity(), VenueDetail1.class);
        intent.putExtra("id",venue.getVenueId());
        startActivity(intent);

    }
    @Override
    public void onResume() {
        super.onResume();

        //navigation drawer highlighting
        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_venue);
        //localReceiver

        LocalBroadcastManager.getInstance(this.getActivity()).registerReceiver(mReceiver, new IntentFilter("syncResponse"));
    }

    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.clear();
            mAdapter.addAll(new Select().from(VenueLocation_db.class).execute());
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
