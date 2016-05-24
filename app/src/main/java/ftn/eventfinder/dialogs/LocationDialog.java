package ftn.eventfinder.dialogs;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import ftn.eventfinder.R;


public class LocationDialog extends AlertDialog.Builder{

	public LocationDialog(Context context) {
		super(context);
		
		setUpDialog();
	}
	
	private void setUpDialog(){
		setTitle("Oops");
	    setMessage("Do you want to enable location, it is disabled");
	    setCancelable(false);
	    
	    setPositiveButton("sure", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				getContext().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				dialog.dismiss();
			}
		});
	    
	    setNegativeButton("no", new DialogInterface.OnClickListener() {
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
