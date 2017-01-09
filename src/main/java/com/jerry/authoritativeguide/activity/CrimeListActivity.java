package com.jerry.authoritativeguide.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.fragment.CrimeFragment;
import com.jerry.authoritativeguide.fragment.CrimeListFragment;
import com.jerry.authoritativeguide.modle.Crime;

/**
 * Created by Jerry on 2017/1/3.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.CallBack, CrimeFragment.CallBack {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_master_detail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        // 通过判断是否含有R.id.fl_detail_container来进行不同方式的向导
        if (findViewById(R.id.fl_detail_container) == null) {
            Intent intent = CrimePagerActivity.getIntent(this, crime.getId());
            startActivity(intent);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = CrimeFragment.newInstance(crime.getId());
            fm.beginTransaction().replace(R.id.fl_detail_container, fragment).commit();
        }
    }

    @Override
    public void onCrimeUpdate(Crime crime) {
        CrimeListFragment fragment = (CrimeListFragment) getSupportFragmentManager().findFragmentById(R.id.fl_container);
        fragment.updateUI();
    }
}
