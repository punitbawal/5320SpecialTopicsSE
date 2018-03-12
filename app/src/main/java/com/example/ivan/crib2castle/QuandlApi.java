package com.example.ivan.crib2castle;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ivan on 2/18/18.
 */

public class QuandlApi extends AsyncTask<String, Void, Double>{

    public QuandlApiResponse delegate=null;
    final String quandlKey = "K68xk9xqc2k8rzUPwJV1";

    protected Double doInBackground(String... zip) {

        double ppsqft = 0;

        // calling Quandl-Zillow API
        String url = "https://www.quandl.com/api/v3/datasets/ZILLOW/Z"+zip[0]+"_MSPFAH.json?api_key="+quandlKey;


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

            JSONObject quandlData = new JSONObject(response.toString());
            ppsqft=quandlData.getJSONObject("dataset").getJSONArray("data").getJSONArray(0).getDouble(1);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        return ppsqft;
    }

    protected void onPostExecute(Double result) {
        delegate.quandlApiFinish(result);
    }
}
