package com.jerry.authoritativeguide.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
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
 * Created by Jerry on 2017/1/18.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {

    private static final String TAG = "PollJobService";

    private PollTask mPollTask;

    private static final int JOB_ID = 1;

    private static final long POLL_INTERVAL = 10 * 1000;

    /**
     * @param params
     * @return 该方法返回false结果表示：“交代的任务我已全力去做，现在做完了。”返回true结果则
     * 表示：“任务收到，正在做，但是还没有做完。”
     */
    @Override
    public boolean onStartJob(JobParameters params) {
        mPollTask = new PollTask();
        mPollTask.execute(params);
        Log.i(TAG, "execute");
        return true;
    }

    /**
     * @param params
     * @return 返回true表示：“任务应该计划在下次继续。”返回false表示：“不管怎样，
     * 事情就到此结束吧，不要计划下次了。”
     */
    @Override
    public boolean onStopJob(JobParameters params) {
        if (mPollTask != null) {
            mPollTask.cancel(true);
        }
        return true;
    }

    private class PollTask extends AsyncTask<JobParameters, Void, Void> {

        @Override
        protected Void doInBackground(JobParameters... params) {
            JobParameters jobParams = params[0];
            // 后台需要检查更新的操作
            checkUpdate();
            // 第二个参数传入true的话，就等于说：“事情这次做不完了，请计划在下次某个时间继续吧。”
            jobFinished(jobParams, false);
            return null;
        }
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
            Log.i(TAG, "Job got a old id!");
        } else {
            Log.i(TAG, "Job got a new id!");
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
     * 检测是否已经计划好任务了
     *
     * @return
     */
    public static boolean isJoBScheduled(Context context) {
        JobScheduler scheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        boolean hasBeenScheduled = false;
        for (JobInfo jobInfo : scheduler.getAllPendingJobs()) {
            if (jobInfo.getId() == JOB_ID) {
                hasBeenScheduled = true;
            }
        }
        return hasBeenScheduled;
    }

    /**
     * 设置计划开启或关闭
     *
     * @param context
     * @param isOn
     */
    public static void setSchedule(Context context, boolean isOn) {
        JobScheduler scheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo jobInfo = new JobInfo.Builder(
                JOB_ID, new ComponentName(context, PollJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                .setPeriodic(POLL_INTERVAL)
//                .setMinimumLatency(5000)
                .setPersisted(false)
                .build();
        if (isOn) {
            scheduler.schedule(jobInfo);
            Log.i(TAG, "schedule");
        } else {
            scheduler.cancel(JOB_ID);
            Log.i(TAG, "cancel");
        }
    }
}
