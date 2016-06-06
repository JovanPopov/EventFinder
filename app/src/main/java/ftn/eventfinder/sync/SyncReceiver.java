package ftn.eventfinder.sync;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import ftn.eventfinder.MainActivity;
import ftn.eventfinder.R;
import ftn.eventfinder.activities.MyPreferenceActivity;
import ftn.eventfinder.tools.ConnectivityTools;



public class SyncReceiver extends BroadcastReceiver {

    private static int notificationID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean allowSyncNotif = sharedPreferences.getBoolean(context.getString(R.string.notif_on_sync_key), false);

        if(intent.getAction().equals(MainActivity.SYNC_DATA)){
            if(allowSyncNotif){
                int resultCode = intent.getExtras().getInt(SyncService.RESULT_CODE);
                int serverResponse = intent.getExtras().getInt(SyncService.SERVER_RESPONSE);
                Log.i("poruka",String.valueOf(serverResponse));
                Bitmap bm = null;

                Intent wiFiintent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                PendingIntent pIntent = PendingIntent.getActivity(context, 0, wiFiintent, 0);

                Intent settingsIntent = new Intent(context, MyPreferenceActivity.class);
                PendingIntent pIntentSettings = PendingIntent.getActivity(context, 0, settingsIntent, 0);

                if(resultCode == ConnectivityTools.TYPE_NOT_CONNECTED){
                    bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_network_wifi);
                    mBuilder.setSmallIcon(R.drawable.ic_action_error);
                    mBuilder.setContentTitle(context.getString(R.string.autosync_problem));
                    mBuilder.setContentText(context.getString(R.string.no_internet));
                    mBuilder.addAction(R.drawable.ic_action_network_wifi, context.getString(R.string.turn_wifi_on), pIntent);
                    mBuilder.addAction(R.drawable.ic_action_settings, context.getString(R.string.turn_notif_on), pIntentSettings);
                    Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show();
                }else if(resultCode == ConnectivityTools.TYPE_MOBILE){
                 /*   bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_action_network_cell);
                    mBuilder.setSmallIcon(R.drawable.ic_action_warning);
                    mBuilder.setContentTitle(context.getString(R.string.autosync_warning));
                    mBuilder.setContentText(context.getString(R.string.connect_to_wifi));
                    mBuilder.addAction(R.drawable.ic_action_network_wifi, context.getString(R.string.turn_wifi_on), pIntent);
                    mBuilder.addAction(R.drawable.ic_action_settings, context.getString(R.string.turn_notif_on), pIntentSettings);*/
                    Toast.makeText(context, "Data sync complete", Toast.LENGTH_LONG).show();
                    Toast.makeText(context, "You are using mobile data", Toast.LENGTH_LONG).show();
                }else if(resultCode == ConnectivityTools.TYPE_WIFI){
                   /* bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
                    mBuilder.setSmallIcon(R.drawable.ic_action_refresh_w);
                    mBuilder.setContentTitle(context.getString(R.string.autosync));
                    mBuilder.setContentText(context.getString(R.string.good_news_sync));
                    mBuilder.addAction(R.drawable.ic_action_settings, context.getString(R.string.turn_notif_on), pIntentSettings);*/
                    Toast.makeText(context, "Data sync complete", Toast.LENGTH_LONG).show();
                }else if(serverResponse == ConnectivityTools.SERVER_RESPONSE_ERROR){
                    Toast.makeText(context, "Sync Complete", Toast.LENGTH_LONG).show();
                }else if(serverResponse == ConnectivityTools.SERVER_RESPONSE_ERROR){
                    Toast.makeText(context, "Failed to connect to our server, please try again later", Toast.LENGTH_LONG).show();
                }



                mBuilder.setLargeIcon(bm);
                // notificationID allows you to update the notification later on.
                mNotificationManager.notify(notificationID, mBuilder.build());
            }
        }

    }

}
