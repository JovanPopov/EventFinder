package ftn.eventfinder.tools;

import android.content.Context;

import java.util.Date;

import ftn.eventfinder.R;


//import com.appspot.elevated_surge_702.crud.Crud;
//import com.appspot.elevated_surge_702.sync.Sync;
//import com.google.api.client.extensions.android.http.AndroidHttp;
//import com.google.api.client.extensions.android.json.AndroidJsonFactory;

public class SyncUtils
{
//	public static Sync buildSyncApi()
//	{
//		return new Sync.Builder(
//				AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
//				//.setRootUrl("http://10.0.3.2:8080/_ah/api/") // 10.0.3.2 je adresa pca sa genymotion emulatora
//				.build();
//	}
//
//	public static Crud buildCrudApi()
//	{
//		return new Crud.Builder(
//				AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
//				//.setRootUrl("http://10.0.3.2:8080/_ah/api/") // 10.0.3.2 je adresa pca sa genymotion emulatora
//				.build();
//	}
	
	public static Date getLastSyncronizationDate(Context context)
	{
		long time = context.getSharedPreferences(context.getString(R.string.sync_preferences), Context.MODE_PRIVATE)
				.getLong(context.getString(R.string.date_last_synchronized), -1);
		
		if(time == -1)
		{
			return null;
		}
		else
		{
			return new Date(time);
		}
	}
	
	public static void setLastSyncronizationDate(Context context, Date date)
	{
		context.getSharedPreferences(context.getString(R.string.sync_preferences), Context.MODE_PRIVATE)
			.edit()
			.putLong(context.getString(R.string.date_last_synchronized), date.getTime())
			.apply();
	}
}
