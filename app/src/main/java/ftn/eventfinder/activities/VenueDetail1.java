package ftn.eventfinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import ftn.eventfinder.R;
import ftn.eventfinder.entities.VenueLocation_db;
import ftn.eventfinder.fragments.EventsInVenueFragment;
import ftn.eventfinder.fragments.EventsListFragment;

public class VenueDetail1 extends AppCompatActivity {
        private String id;
        private VenueLocation_db venue;
        FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_detail1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        venue= new Select().from(VenueLocation_db.class).where("venueId = ?", id).executeSingle();
        Log.i("ven", id);
        setTitle("");
        fillData(id);

        fab=(FloatingActionButton) findViewById(R.id.fabFavVen);
        assert fab != null;
        if(venue.isFavourite()) fab.setImageResource(R.drawable.ic_favorite_white_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!venue.isFavourite()) {
                    venue.setFavourite(true);
                    venue.save();
                    fab.setImageResource(R.drawable.ic_favorite_white_24dp);

                    Snackbar.make(view, "Venue added to favourites", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    venue.setFavourite(false);
                    venue.save();

                    fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Snackbar.make(view, "Venue removed from favourites", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                }
            }
        });
    }
    private void fillData(String id){


        TextView title = (TextView) findViewById(R.id.venue_detail_name);
        title.setText(venue.getVenueName());
        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+venue.getVenueId()));
                startActivity(browserIntent);
                return false;
            }
        });

        ImageView imageCover = (ImageView) findViewById(R.id.venue_detail_cover_picture);

        if(venue.getVenueProfilePicture() != null)
        {


            Picasso.with(this)
                    .load(venue.getVenueCoverPicture())
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageCover);
        }

        ImageView imageProfile = (ImageView) findViewById(R.id.venue_detail_profile_picture);

        if(venue.getVenueProfilePicture() != null)
        {


            Picasso.with(this)
                    .load(venue.getVenueProfilePicture())
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageProfile);
        }


        //fragment transition



        FragmentManager fm = this.getSupportFragmentManager();
        Fragment existingFragment = fm.findFragmentByTag("venueEvents");
        if(existingFragment==null) {
            Bundle args = new Bundle();
            EventsInVenueFragment toFragment = EventsInVenueFragment.newInstance();
            args.putString("id", id);
            toFragment.setArguments(args);
            FragmentTransaction transaction = fm
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragmentEventsInVenue, toFragment, "venueEvents");
            //transaction.addToBackStack(null);
            transaction.commit();
        }else{
            FragmentTransaction transaction = fm
                    .beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.fragmentEventsInVenue,existingFragment, "venueEvents");
            //transaction.addToBackStack(null);
            transaction.commit();
        }


    }
}
