package ftn.eventfinder.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.RetrofitInt.EventsInterface;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.Tag_db;
import ftn.eventfinder.model.Tag;
import ftn.eventfinder.sync.GetTagsService;
import ftn.eventfinder.sync.UpVoteTag;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EventDetail extends AppCompatActivity {

    private String id;
    private Event_db event;
    String tags="";
    FloatingActionButton fab;
    Context context;
    int tagId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context=this;
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



        EditText editText = (EditText) findViewById(R.id.tagInput);
        assert editText != null;
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event1) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //Toast.makeText(getApplicationContext(), v.getText(), Toast.LENGTH_SHORT).show();
                    handled = true;
                    String inputTag = v.getText().toString();
                    if (!inputTag.equals("")) {
                        Tag tag = new Tag(event.getEventId(), event.getVenueLocation().getVenueId(), inputTag);


                        final String BASE_URL = "http://188.2.87.248:4000/rest/";

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(BASE_URL)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();


                        EventsInterface service = retrofit.create(EventsInterface.class);
                        Call<Tag> call = service.addTag(tag);
                        call.enqueue(new Callback<Tag>() {
                            @Override
                            public void onResponse(Call<Tag> call, Response<Tag> response) {
                                Tag tag = response.body();
                                Log.i("CallBack", " response is " + response.message());
                                Intent si = new Intent(context, GetTagsService.class);
                                si.putExtra("value", tag.getValue());
                                si.putExtra("eventId", tag.getEventId());
                                startService(si);
                                // startService(new Intent(context, GetTagsService.class));
                            }

                            @Override
                            public void onFailure(Call<Tag> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "problem connecting to our server", Toast.LENGTH_SHORT).show();
                                Log.e("CallBack", "SyncTask", t);
                            }

                        });

                    }
                    v.setText("");
                    return false;
                }
                return true;
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();
        EditText  edit = (EditText ) findViewById(R.id.tagInput);
        edit.clearFocus();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("tagsResponse"));
    }
    public BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshTags();

        }
    };


    private void fillData(String id){


        TextView title = (TextView) findViewById(R.id.event_detail_name);
        title.setText(event.getEventName());

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

        TextView stats1 = (TextView) findViewById(R.id.event_stats1);
        stats1.setText("going: " + event.getEventStats().getAttendingCount());

        TextView stats2 = (TextView) findViewById(R.id.event_stats2);
        stats2.setText("interested: "  + event.getEventStats().getMaybeCount());

        TextView desc = (TextView) findViewById(R.id.eventDesc);
        desc.setText(event.getEventDescription());
        TextView descb = (TextView) findViewById(R.id.descB);

        final boolean[] showMore = {false};
        descb.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showMore[0] =!showMore[0];

                if(showMore[0]) {
                    TextView desc = (TextView) findViewById(R.id.eventDesc);
                    desc.getLayoutParams().height = Toolbar.LayoutParams.WRAP_CONTENT;
                    TextView descb = (TextView) findViewById(R.id.descB);
                    descb.setText("show less");
                }else{

                    TextView desc = (TextView) findViewById(R.id.eventDesc);
                    desc.getLayoutParams().height = 100;
                    TextView descb = (TextView) findViewById(R.id.descB);
                    descb.setText("show more");
                }
                return false;
            }
        });



        TextView venue = (TextView) findViewById(R.id.event_venue);
        venue.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event1) {
                Intent intent = new Intent(context, VenueDetail1.class);
                intent.putExtra("id",event.getVenueLocation().getVenueId());
                startActivity(intent);
                return false;
            }
        });
        TextView face = (TextView) findViewById(R.id.event_face);
        face.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event1) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/"+event.getEventId()));
                startActivity(browserIntent);
                return false;
            }
        });

        TextView time = (TextView) findViewById(R.id.event_time);

        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date1=null;
        try {
            date1 = incomingFormat.parse(event.getEventStarttime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outgoingFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.getDefault());
        SimpleDateFormat outgoingFormat1 = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

        time.setText(outgoingFormat.format(date1) + " at " + outgoingFormat1.format(date1));


        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        int value = date1.before(currentDate)? Color.RED:Color.BLACK;
        title.setTextColor(value);

        TextView location = (TextView) findViewById(R.id.event_location);
        String loca="";
        if(event.getVenueLocation().getCity()!=null) loca=loca.concat(event.getVenueLocation().getCity() + ", ");
        if(event.getVenueLocation().getStreet()!=null) loca= loca.concat(event.getVenueLocation().getStreet());
        location.setText(loca);






        refreshTags();


}

    private void refreshTags(){

        LinearLayout ll = (LinearLayout) findViewById(R.id.tagsContent);
        ll.removeAllViews();

        //ta.setText(tags);

        List<Tag_db> ttt=event.getTags();
        Collections.sort(ttt, new Comparator<Tag_db>() {
            @Override public int compare(Tag_db p1, Tag_db p2) {
                return p2.getWeight() - p1.getWeight(); // Ascending
            }

        });
        Log.i("tags",String.valueOf(ttt.size()) + "Broj tagova");
        if(!ttt.isEmpty()) {

            for (Tag_db t:ttt) {
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
