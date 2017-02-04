package com.jerry.authoritativeguide.activity;

import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.DragAndDrawFragment;

/**
 * Created by Jerry on 2017/2/4.
 */

public class DragAndDrawActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return DragAndDrawFragment.newInstance();
    }
}
