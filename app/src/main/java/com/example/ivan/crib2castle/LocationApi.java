package com.example.ivan.crib2castle;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ivan on 3/11/18.
 */

public class LocationApi extends AsyncTask<String, Void, Double[]> {

    final String apiKey = "AIzaSyBPdbRNXDSbWHUCnpdo6P9oKDDp1ZoT8sI";
    public LocationApiResponse delegate = null;

    protected Double[] doInBackground(String... address) {

        // converts address into API format
        String[] addressParts = address[0].split(" ");
        String apiAddressString = "";
        for(String part:addressParts) {
            String simplePart = part.replace(",","");
            apiAddressString += "+"+simplePart+",";
        }
        apiAddressString = apiAddressString.substring(0, apiAddressString.length()-1);


        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="+apiAddressString+"&key="+apiKey;

        Log.d("C2C", url);

        Double longitude = 0.0;
        Double latitude = 0.0;

        try {

            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();


            // GET request
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            // reading response
            StringBuffer response = new StringBuffer();
            String inputLine;
            while((inputLine = br.readLine()) != null) response.append(inputLine);
            br.close();

            JSONObject mapsData = new JSONObject(response.toString());
            String status = mapsData.getString("status");
            if(status.equals("OK")) {
                latitude=mapsData.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                longitude=mapsData.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Double[] {latitude, longitude};
    }

    protected void onPostExecute(Double[] result) {
        delegate.locationApiFinish(result);
    }
}
