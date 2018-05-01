package com.cse5320.c2c.crib2castle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Ivan on 3/18/18.
 */

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    DownloadImageResponse delegate;
    int index;
    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(urls[0].toString());
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.d("C2C", "Error getting bitmap", e);
        }

        index = Integer.parseInt(urls[1]);
        return bm;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        delegate.downloadImageFinish(bitmap, index);
    }
}
