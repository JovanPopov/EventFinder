package ftn.eventfinder.adapters;

/**
 * Created by Jovan on 4.6.2016.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.Tag_db;
import ftn.eventfinder.entities.VenueLocation_db;

public class VenueListAdapter extends ArrayAdapter {

    private Context context;
    private boolean useList = true;

    public VenueListAdapter(Context context, List items) {
        super(context, android.R.layout.simple_list_item_1, items);
        this.context = context;


    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder {
        TextView titleText;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {

        VenueLocation_db venue = (VenueLocation_db) getItem(position);
        View view = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            if (useList) {
                view = mInflater.inflate(R.layout.info_venue, null);
            } else {
                view = mInflater.inflate(R.layout.info_venue, null);
            }


        } else {
            view = convertView;
        }







        TextView title = (TextView) view.findViewById(R.id.venue_name);
        title.setText(venue.getVenueName());

        TextView location = (TextView) view.findViewById(R.id.venue_location);
        String loca="";
        if(venue.getCity()!=null) loca=loca.concat(venue.getCity() + ", ");
        if(venue.getStreet()!=null) loca= loca.concat(venue.getStreet());
        location.setText(loca);

        TextView events = (TextView) view.findViewById(R.id.venue_events);
        events.setText(String.valueOf(venue.events().size()));



        ImageView image = (ImageView) view.findViewById(R.id.venue_picture);

        if(venue.getVenueProfilePicture() != null)
        {


            //Picasso.with(view.getContext()).load(event.getEventProfilePicture()).into(image);
            Picasso.with(view.getContext())
                    .load(venue.getVenueProfilePicture())
                    .placeholder(R.drawable.image_placeholder)
                    .into(image);
        }


        TextView tags = (TextView) view.findViewById(R.id.venue_info_tags);
        String s="";

        List<Tag_db> ttt = new ArrayList<Tag_db>();
        List<Event_db> events1 = venue.events();
        List<Tag_db> results = new ArrayList<Tag_db>();
      /*  for(Event_db event : events1) {

            ttt.addAll(event.getTags());
        }*/

        ttt= new Select().from(Tag_db.class).where("venueId = ?", venue.getVenueId()).execute();

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


        int ip=0;
        for (Tag_db t : results) {
            s = s + "#" + t.getValue() + " ";
            ip++;
            if(ip==3)break;
        }



        tags.setText(s);


        return view;

    }

}