package com.jerry.authoritativeguide.service;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.activity.PhotoGalleryActivity;
import com.jerry.authoritativeguide.model.GalleryItem;
import com.jerry.authoritativeguide.util.FlickrFetchr;
import com.jerry.authoritativeguide.util.InternetUtil;
import com.jerry.authoritativeguide.util.QuerySharePreferences;

import java.util.List;

/**
 * Created by Jerry on 2017/1/17.
 */

public class PollService extends IntentService {

    public static final String ACTION_SHOW_NOTIFICATION = "action_show_notification";

    public static final String PERMISSION_PRIVATE = "com.jerry.authoritativeguide.photogallery.Private";

    public static final String REQUEST_CODE = "request_code";
    public static final String NOTIFICATION = "notification";

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
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
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
            // 创建Notification,是否发送要看应用是否在前台
            Notification notification = createNotification();

            showBackgroundNotification(0, notification);
        }

        QuerySharePreferences.setLastResultId(getApplicationContext(), resultId);
    }

    /**
     * 如果应用在后台启用，那么就生成显示的Notification
     *
     * @param requestCode
     * @param notification
     */
    private void showBackgroundNotification(int requestCode, Notification notification) {

        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(NOTIFICATION, notification);
        sendOrderedBroadcast(intent, PERMISSION_PRIVATE, null, null, Activity.RESULT_OK,
                null, null);
    }

    /**
     * 创建PendingIntent
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification createNotification() {
        Resources resources = getResources();
        Intent intent = PhotoGalleryActivity.newIntent(getApplicationContext());
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

        Notification notification = new Notification.Builder(getApplication().getApplicationContext())
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        return notification;
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
        QuerySharePreferences.setAlarmOn(context, isOn);
    }

    /**
     * 判断定时器是否开启
     *
     * @param context
     * @return
     */
    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pi != null;
    }
}
