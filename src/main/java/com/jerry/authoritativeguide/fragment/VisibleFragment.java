package com.jerry.authoritativeguide.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.jerry.authoritativeguide.service.PollService;

/**
 * Created by Jerry on 2017/1/19.
 */

public abstract class VisibleFragment extends Fragment {

    private static final String TAG = "VisibleFragment";


    private BroadcastReceiver onShowNotification = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(getActivity(), "Got a broadcast", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(onShowNotification, intentFilter,
                PollService.PERMISSION_PRIVATE, null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(onShowNotification);
    }
}
