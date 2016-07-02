package ftn.eventfinder.adapters;

/**
 * Created by Jovan on 4.6.2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Activity;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.entities.Event_db;
import ftn.eventfinder.entities.Tag_db;

public class EventListAdapter  extends ArrayAdapter {

    private Context context;
    private boolean useList = true;

    public EventListAdapter(Context context, List items) {
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

        Event_db event = (Event_db) getItem(position);
        View view = null;

        // This block exists to inflate the settings list item conditionally based on whether
        // we want to support a grid or list view.
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            if (useList) {
                view = mInflater.inflate(R.layout.info_window_layout, null);
            } else {
                view = mInflater.inflate(R.layout.info_window_layout, null);
            }


        } else {
            view = convertView;
        }







        TextView title = (TextView) view.findViewById(R.id.event_name);
        title.setText(event.getEventName());

        TextView date = (TextView) view.findViewById(R.id.event_date);

        SimpleDateFormat incomingFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date date1=null;
        try {
            date1 = incomingFormat.parse(event.getEventStarttime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat outgoingFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy", java.util.Locale.getDefault());
        SimpleDateFormat outgoingFormat1 = new SimpleDateFormat("HH:mm", java.util.Locale.getDefault());

        date.setText(outgoingFormat.format(date1) + " at " + outgoingFormat1.format(date1));


        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        int value = date1.before(currentDate)?Color.RED:Color.BLACK;
        title.setTextColor(value);

        TextView place = (TextView) view.findViewById(R.id.event_location);
        place.setText(event.getVenueLocation().getVenueName());

        ImageView image = (ImageView) view.findViewById(R.id.event_picture);

        if(event.getEventProfilePicture() != null)
        {


            //Picasso.with(view.getContext()).load(event.getEventProfilePicture()).into(image);
            Picasso.with(view.getContext())
                    .load(event.getEventProfilePicture())
                    .placeholder(R.drawable.image_placeholder)
                    .into(image);
        }



        TextView tags = (TextView) view.findViewById(R.id.event_info_tags);
        String s="";
        List<Tag_db> ta=event.getTags();
        Collections.sort(ta, new Comparator<Tag_db>() {
            @Override public int compare(Tag_db p1, Tag_db p2) {
                return p2.getWeight() - p1.getWeight(); // Ascending
            }
        });
        int ip=0;
        for (Tag_db t :ta) {
            s = s + "#" + t.getValue() + " ";
            ip++;
            if(ip==3)break;
        }



        tags.setText(s);



        return view;

    }

}