package ftn.eventfinder.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import ftn.eventfinder.R;

/**
 * Created by Jovan on 8.6.2016.
 */
public class SortDialog extends AlertDialog.Builder {
    private SharedPreferences.Editor editor;


    public SortDialog(Context context) {
        super(context);
        setUpDialog();
    }

    private void setUpDialog(){
        setTitle("Sort by");
        //setMessage("Do you want to enable location, it is disabled");
        setCancelable(false);


        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = sharedpreferences.edit();

        setItems(R.array.pref_sort_values, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                switch(which){
                case 0:
                    editor.putString("sort", "eventStarttime ASC");
                    editor.commit();

                    break;
                case 1 :
                    editor.putString("sort", "eventName ASC");
                    editor.commit();
                    break;


                }
                dialog.dismiss();
                Intent intent = new Intent("order_criteria");
                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
                // The 'which' argument contains the index position
                // of the selected item
            }
        });

/*
        setPositiveButton("ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                dialog.dismiss();
            }
        });*/

        setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

    }

    public AlertDialog prepareDialog(){
        AlertDialog dialog = create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }


}
