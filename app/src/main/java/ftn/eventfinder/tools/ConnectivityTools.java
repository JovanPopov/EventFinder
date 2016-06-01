package ftn.eventfinder.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class ConnectivityTools {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    public static int SERVER_RESPONSE_OK = 1;
    public static int SERVER_RESPONSE_ERROR = 0;


    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static int calculateTimeTillNextSync(int minutes){
        return 1000 * 60 * minutes;
    }
}
