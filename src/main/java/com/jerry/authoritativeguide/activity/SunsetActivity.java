package com.jerry.authoritativeguide.activity;

import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.SunsetFragment;

/**
 * Created by Jerry on 2017/2/6.
 */

public class SunsetActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return SunsetFragment.newInstance();
    }
}
