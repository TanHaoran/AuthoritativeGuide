package com.jerry.authoritativeguide.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
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

    /**
     * 用来缓存图片的类
     */
    private LruCache<String, Bitmap> mMemoryCache;

    public interface ThumbnailDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;

        // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
        // LruCache通过构造函数传入缓存值，以KB为单位。
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // 使用最大可用内存值的1/8作为缓存的大小。
        int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                return bitmap.getByteCount() / 1024;
            }
        };
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
        // 先检测缓存中是否存在，不存在再去加载
        Bitmap b = getBitmapFromMemCache(url);
        if (b == null) {
            try {
                byte[] bytes = new FlickrFetchr().getUrlBytes(url);
                b = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                // 执行完毕之后需要将缓存放入LruCache中
                if (url != null && b != null) {
                    addBitmapToMemoryCache(url, b);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final Bitmap bitmap = b;
        mResponseHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mConcurrentMap.get(target) != url || mHasQuit) {
                    return;
                }
                mConcurrentMap.remove(target);
                // 为防止有时候会将一个被回收的空的bitmap设置到界面上
                if (bitmap != null) {
                    mThumbnailDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            }
        });
    }

    /**
     * 将下载任务添加到队列中，并设置消息和目标Handler
     *
     * @param target
     * @param url
     */
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

    /**
     * 放入图片缓存
     *
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (key == null) {
            return;
        }
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    /**
     * 取出图片缓存
     *
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemCache(String key) {
        if (key == null) {
            return null;
        }
        return mMemoryCache.get(key);
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    @Override
    public boolean quit() {
        mHasQuit = true;
        return super.quit();
    }
}
