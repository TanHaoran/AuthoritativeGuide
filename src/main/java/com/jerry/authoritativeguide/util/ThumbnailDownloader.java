package com.jerry.authoritativeguide.util;

import android.os.HandlerThread;
import android.util.Log;

/**
 * Created by Jerry on 2017/1/13.
 */

public class ThumbnailDownloader<T> extends HandlerThread {

    private static  final  String TAG = "ThumbnailDownloader";

    private boolean mHasQuit = false;

    public ThumbnailDownloader() {
        super(TAG);
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a url : " + url);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();

    }
}
