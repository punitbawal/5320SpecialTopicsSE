package com.example.ivan.crib2castle;

import android.graphics.Bitmap;

/**
 * Created by Ivan on 3/18/18.
 */

public interface DownloadImageResponse {
    public void downloadImageFinish(Bitmap bitmap, int index);
}
