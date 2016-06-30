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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.activities.VenueDetail1;
import ftn.eventfinder.adapters.VenueListAdapter;
import ftn.eventfinder.entities.VenueLocation_db;

/**
 * Created by Jovan on 4.6.2016.
 */
public class VenuesListFragment extends ListFragment {


    VenueListAdapter mAdapter;
    boolean favourite;
    MenuItem item;

    public static VenuesListFragment newInstance() {

        VenuesListFragment mpf = new VenuesListFragment();


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
            mAdapter = new VenueListAdapter(getActivity(), new Select().from(VenueLocation_db.class).execute());
        }else{
            List<VenueLocation_db> list = new Select().from(VenueLocation_db.class).where("favourite = ?", true).execute();
            if (!list.isEmpty()) {
                mAdapter = new VenueListAdapter(getActivity(), list);
            }else{
                mAdapter = new VenueListAdapter(getActivity(), new Select().from(VenueLocation_db.class).execute());
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

        if(favourite) {
            List<VenueLocation_db> list = new Select().from(VenueLocation_db.class).where("favourite = ?", true).execute();
            if (!list.isEmpty()) {

                mAdapter.clear();
                mAdapter.addAll(list);
                mAdapter.notifyDataSetChanged();

            } else {
                mAdapter.clear();
                mAdapter.addAll(new Select().from(VenueLocation_db.class).execute());
                mAdapter.notifyDataSetChanged();
                favourite=false;
            }
        }else{
            mAdapter.clear();
            mAdapter.addAll(new Select().from(VenueLocation_db.class).execute());
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
                mAdapter.addAll(new Select().from(VenueLocation_db.class).execute());
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

                List<VenueLocation_db> list = new Select().from(VenueLocation_db.class).where("favourite = ?", true).execute();
                if (!list.isEmpty()) {
                    mAdapter.clear();
                    mAdapter.addAll(list);
                    mAdapter.notifyDataSetChanged();
                } else {
                    favourite = !favourite;
                    Toast.makeText(getActivity(), "No venues favourited", Toast.LENGTH_SHORT).show();
                }

            }else{

                mAdapter.clear();
                mAdapter.addAll(new Select().from(VenueLocation_db.class).execute());
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
