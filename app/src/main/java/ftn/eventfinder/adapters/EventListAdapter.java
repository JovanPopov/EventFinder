package ftn.eventfinder.adapters;

/**
 * Created by Jovan on 4.6.2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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
import java.util.Date;
import java.util.List;

import ftn.eventfinder.R;
import ftn.eventfinder.entities.Event_db;

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







        TextView title = (TextView) view.findViewById(R.id.marker_name);
        title.setText(event.getEventName());

        TextView date = (TextView) view.findViewById(R.id.marker_date);

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

        TextView place = (TextView) view.findViewById(R.id.marker_place);
        place.setText(event.getVenueName());

        ImageView image = (ImageView) view.findViewById(R.id.marker_picture);

        if(event.getEventProfilePicture() != null)
        {


            //Picasso.with(view.getContext()).load(event.getEventProfilePicture()).into(image);
            Picasso.with(view.getContext())
                    .load(event.getEventProfilePicture())
                    .placeholder(R.drawable.image_placeholder)
                    .into(image);
        }





        return view;

    }

}