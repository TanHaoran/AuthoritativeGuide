package com.jerry.authoritativeguide.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jerry.authoritativeguide.service.PollService;
import com.jerry.authoritativeguide.util.QuerySharePreferences;

/**
 * Created by Jerry on 2017/1/19.
 */

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Receive an intent!");
        // 获取保存的是否启动的值，然后设置是否启动
        boolean isAlarmOn = QuerySharePreferences.isAlarmOn(context);
        PollService.setServiceAlarm(context, isAlarmOn);
    }
}
