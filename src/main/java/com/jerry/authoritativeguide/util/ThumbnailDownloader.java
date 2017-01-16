package com.jerry.authoritativeguide.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Jerry on 2017/1/13.
 */

public class ThumbnailDownloader<T> extends HandlerThread {

    private static final int MESSAGE_DOWNLOAD = 0;

    private static final String TAG = "ThumbnailDownloader";

    private boolean mHasQuit = false;

    private Handler mRequestHandler;
    private Handler mResponseHandler;

    private ConcurrentMap<T, String> mConcurrentMap = new ConcurrentHashMap<>();

    private ThumbnailDownloadListener mThumbnailDownloadListener;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    public void setThumbnailDownloadListener(ThumbnailDownloadListener thumbnailDownloadListener) {
        mThumbnailDownloadListener = thumbnailDownloadListener;
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T) msg.obj;
                    Log.i(TAG, "Got a url from handler: " + mConcurrentMap.get(target));
                    handleRequest(target);
                }
            }
        };
    }

    /**
     * 将传过来的目标解析出来图片
     *
     * @param target
     */
    private void handleRequest(final T target) {
        final String url = mConcurrentMap.get(target);
        try {
            byte[] bytes = new FlickrFetchr().getUrlBytes(url);
            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            Log.i(TAG, "Bitmap created!");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mConcurrentMap.get(target) != url || mHasQuit) {
                        return;
                    }
                    mConcurrentMap.remove(target);
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void queueThumbnail(T target, String url) {
        Log.i(TAG, "Got a url : " + url);
        // 如果url为空，表明不需要下载这个链接，就移除集合
        if (url == null) {
            mConcurrentMap.remove(target, url);
        } else {
            mConcurrentMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    public  void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
}
