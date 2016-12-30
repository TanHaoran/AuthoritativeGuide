package com.jerry.authoritativeguide.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.fragment.CrimeFragment;

public class CrimeActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime);

        FragmentManager fm = getSupportFragmentManager();

        // 首先检查是否已经显示出来了，没有显示就创建一个新的
        Fragment fragment = fm.findFragmentById(R.id.fl_container);
        if (fragment == null) {
            fragment = new CrimeFragment();
            fm.beginTransaction().add(R.id.fl_container, fragment).commit();
        }
    }
}
