package ftn.eventfinder.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import ftn.eventfinder.entities.Event_db;

public class EventDetail extends AppCompatActivity {

    private String id;
    private Event_db event;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        id = extras.getString("id");
        event = new Select().from(Event_db.class).where("eventId = ?", id).executeSingle();
        Log.i("ven", id);
        setTitle("");
        fillData(id);

        fab=(FloatingActionButton) findViewById(R.id.fabFav);
        assert fab != null;
        if(event.isFavourite()) fab.setImageResource(R.drawable.ic_favorite_white_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!event.isFavourite()) {
                    event.setFavourite(true);
                    event.save();
                    fab.setImageResource(R.drawable.ic_favorite_white_24dp);

                    Snackbar.make(view, "Event added to favourites", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }else{
                    event.setFavourite(false);
                    event.save();

                    fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
                    Snackbar.make(view, "Event removed from favourites", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();

                }
            }
        });

    }

    private void fillData(String id){


        TextView title = (TextView) findViewById(R.id.event_detail_name);
        title.setText(event.getEventName());
        title.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event1) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebo o k.com/"+event.getEventId()));
                startActivity(browserIntent);
                return false;
            }
        });

        ImageView imageCover = (ImageView) findViewById(R.id.event_detail_cover_picture);

         if(event.getEventProfilePicture()!=null)
        {


            Picasso.with(this)
                    .load(event.getEventCoverPicture())
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageCover);
        }

        ImageView imageProfile = (ImageView) findViewById(R.id.event_detail_profile_picture);

         if(event.getEventProfilePicture()!=null)
        {


            Picasso.with(this)
                    .load(event.getEventProfilePicture())
                    .placeholder(R.drawable.image_placeholder)
                    .into(imageProfile);
        }



}


}
