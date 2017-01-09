package com.jerry.authoritativeguide.activity;

import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.BeatBoxFragment;

/**
 * Created by Jerry on 2017/1/9.
 */
public class BeatBoxActivity extends SingleFragmentActivity {


    @Override
    protected Fragment createFragment() {
        return BeatBoxFragment.newInstance();
    }
}
