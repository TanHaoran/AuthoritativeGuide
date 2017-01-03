package com.jerry.authoritativeguide.activity;

import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.CrimeListFragment;

/**
 * Created by Jerry on 2017/1/3.
 */

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

}
