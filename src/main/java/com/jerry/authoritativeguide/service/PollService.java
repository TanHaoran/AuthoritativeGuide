package com.jerry.authoritativeguide.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.SystemClock;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.activity.PhotoGalleryActivity;
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

    private static final long POLL_INTERVAL = 60 * 1000;

    public static Intent newIntent(Context context) {
        return new Intent(context, PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Receive an intent : " + intent);

        // 后台需要检查更新的操作
        checkUpdate();
    }

    /**
     * 后台需要检查更新的操作
     */
    private void checkUpdate() {
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
            Log.i(TAG, "Got a new id!");
            createPendingIntent();
        }

        QuerySharePreferences.setLastResultId(getApplicationContext(), resultId);
    }

    /**
     * 创建PendingIntent
     */
    private void createPendingIntent() {
        Resources resources = getResources();
        Intent intent = PhotoGalleryActivity.newIntent(getApplicationContext());
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            Notification notification = new Notification.Builder(this)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification);
        }
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
