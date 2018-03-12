package com.example.ivan.crib2castle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Ivan on 3/3/18.
 */

public class Utils {

    public double getDistanceFromLatLng(double lat1, double lng1, double lat2, double lng2) {
        double R = 3959; // Radius of the earth in mi
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLng = deg2rad(lng2-lng1);
        double a =  Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLng/2) * Math.sin(dLng/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }


    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    public boolean checkForNetworkConnection(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
