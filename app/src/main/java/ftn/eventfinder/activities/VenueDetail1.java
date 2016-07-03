package ftn.eventfinder.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.Tag_db;
import ftn.eventfinder.entities.VenueLocation_db;
import ftn.eventfinder.fragments.EventsInVenueFragment;
import ftn.eventfinder.fragments.EventsListFragment;
import ftn.eventfinder.sync.UpVoteTag;

public class VenueDetail1 extends AppCompatActivity {
        private String id;
        private VenueLocation_db venue;
        FloatingActionButton fab;
        Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context=this;
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


        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ImageView imageCover = (ImageView) findViewById(R.id.venue_detail_cover_picture);
            imageCover.setVisibility(View.GONE);
        /*    ImageView imageProfile = (ImageView) findViewById(R.id.venue_detail_profile_picture);
            imageProfile.setVisibility(View.GONE);*/
            TextView te = (TextView) findViewById(R.id.venue_detail_list_string);
            te.setVisibility(View.GONE);
        }


    }
    private void fillData(String id){


        TextView title = (TextView) findViewById(R.id.venue_detail_name);
        title.setText(venue.getVenueName());

        TextView face = (TextView) findViewById(R.id.venue_face);
        assert face != null;
        face.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+venue.getVenueId()));
                startActivity(browserIntent);
                return false;
            }
        });

        TextView loc = (TextView) findViewById(R.id.venue_location);
        String loca="";
        if(venue.getCity()!=null) loca=loca.concat(venue.getCity() + ", ");
        if(venue.getStreet()!=null) loca= loca.concat(venue.getStreet());
        loc.setText(loca);




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

        refreshTags();
    }

    private void refreshTags(){

        LinearLayout ll = (LinearLayout) findViewById(R.id.tagsContentVenue);
        ll.removeAllViews();

        //ta.setText(tags);
        List<Tag_db> ttt = new ArrayList<Tag_db>();
        List<Event_db> events = venue.events();
        List<Tag_db> results = new ArrayList<Tag_db>();
      /*  for(Event_db event : events) {

            ttt.addAll(event.getTags());
        }*/


        ttt= new Select().from(Tag_db.class).where("venueId = ?", id).execute();

            for (Tag_db tag1: ttt) {
                int num=0;
                for (Tag_db tag2: ttt) {

                    if(tag1.getValue().equals(tag2.getValue())) num++;


                }
                if(num>1) {
                    boolean helper=true;
                    for (Tag_db t : results) {
                        if(t.getValue().equals(tag1.getValue())){
                            helper=false;
                            break;
                        }
                    }

                    if(helper)results.add(tag1);
                }
            }





        Collections.sort(results, new Comparator<Tag_db>() {
            @Override public int compare(Tag_db p1, Tag_db p2) {
                return p2.getWeight() - p1.getWeight(); // Ascending
            }

        });




        if(!results.isEmpty()) {

            for (Tag_db t:results) {
                TextView tV= new TextView(this);
                Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.WRAP_CONTENT);
                tV.setText("#"+t.getValue());
                tV.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                tV.setPadding(5, 5, 5, 5);// in pixels (left, top, right, bottom)
                layoutParams.setMargins(10, 10, 10, 10); // (left, top, right, bottom)
                tV.setLayoutParams(layoutParams);
                tV.setId(t.getTagId());
                ll.addView(tV);

                tV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id=v.getId();
                        Tag_db tag= new Select().from(Tag_db.class).where("tagId = ?", id).executeSingle();
                        if(!tag.isVote()) {
                            Intent si = new Intent(context, UpVoteTag.class);
                            si.putExtra("id", id);
                            startService(si);
                            Toast.makeText(getApplicationContext(), "Tag Up voted", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(getApplicationContext(), "You can not up vote the same tag twice", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return  true;
        }
        return super.onOptionsItemSelected(item);

    }
}
