package com.jerry.authoritativeguide.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.jerry.authoritativeguide.modle.GalleryItem;
import com.jerry.authoritativeguide.util.FlickrFetchr;
import com.jerry.authoritativeguide.util.InternetUtil;
import com.jerry.authoritativeguide.util.QuerySharePreferences;

import java.util.List;

/**
 * Created by Jerry on 2017/1/17.
 */

public class PollService extends IntentService {

    private static final String TAG = "PollService";

    private static final int POLL_INTERVAL = 60 * 1000;

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Receive an intent : " + intent);

        if (!InternetUtil.isNetworkAvailableAndConnected(getApplicationContext())) {
            return;
        }

        String query = QuerySharePreferences.getStoredQuery(getApplicationContext());
        String lastResultId = QuerySharePreferences.getLastResultId(getApplicationContext());

        List<GalleryItem> items;

        if (query == null) {
            items = new FlickrFetchr().fetchRecentPhotos(1);
        } else {
            items = new FlickrFetchr().searchPhotos(query);
        }

        if (items.size() == 0) {
            return;
        }

        String resultId = items.get(0).getId();

        if (resultId.equals(lastResultId)) {
            Log.i(TAG, "Got a old id!");
        } else {
            Log.i(TAG, "Got a new old id!");
        }

        QuerySharePreferences.setLastResultId(getApplicationContext(), resultId);
    }

    /**
     * 设置后台服务是否开启
     *
     * @param context
     * @param isOn
     */
    public static void setServiceAlarm(Context context, boolean isOn) {
        Intent intent = newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime(), POLL_INTERVAL, pi);
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
        }
    }

    /**
     * 判断定时器是否开启
     * @param context
     * @return
     */
    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
