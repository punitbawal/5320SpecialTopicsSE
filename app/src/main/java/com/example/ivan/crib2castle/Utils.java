package com.example.ivan.crib2castle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.util.Random;

/**
 * Created by Ivan on 3/3/18.
 */

public class Utils {


    /*
     * generates a random string of length n
     */
    public String randString(int n) {
        String randString = "";
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random r = new Random();
        for(int i=0; i<n; i++){
            randString += alphabet.charAt(r.nextInt(alphabet.length()));
        }
        return randString;
    }



    /*
     * formats number to currency format
     */
    public String numberToCurrency(long number) {
        String strCurrency = "";
        long hardNumber = number;
        for(int i=0; i<Math.log10(hardNumber)+0.00000001; i++) {
            strCurrency = String.valueOf(number % 10)+strCurrency;
            if(i%3 == 2) strCurrency = ","+strCurrency;
            number /= 10;
        }
        if(strCurrency.length() == 0) return strCurrency;
        if(strCurrency.charAt(0) == ',') strCurrency = strCurrency.substring(1);
        return strCurrency;
    }


    /*
     * calculates distance between two points (lng, lat) in miles
     */
    public double getDistanceFromLatLng(double lat1, double lng1, double lat2, double lng2) {
        double R = 3959; // Radius of the earth in mi
        double dLat = degToRad(lat2-lat1);  // deg2rad below
        double dLng = degToRad(lng2-lng1);
        double a =  Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(degToRad(lat1)) * Math.cos(degToRad(lat2)) *
                                Math.sin(dLng/2) * Math.sin(dLng/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }


    public double degToRad(double deg) {
        return deg * (Math.PI/180);
    }

    public boolean checkForNetworkConnection(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
