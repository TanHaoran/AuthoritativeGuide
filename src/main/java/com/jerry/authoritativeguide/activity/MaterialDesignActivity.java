package com.jerry.authoritativeguide.activity;

import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.MaterialDesignFragment;

/**
 * Created by Jerry on 2017/2/7.
 */

public class MaterialDesignActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return MaterialDesignFragment.newInstance();
    }
}
