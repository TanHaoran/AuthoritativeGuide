package com.jerry.authoritativeguide.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.fragment.CrimeFragment;
import com.jerry.authoritativeguide.fragment.CrimeListFragment;
import com.jerry.authoritativeguide.modle.Crime;
import com.jerry.authoritativeguide.util.CrimeLab;

import java.util.ArrayList;
import java.util.UUID;

public class CrimePagerActivity extends AppCompatActivity {

    private static final String TAG = "CrimePagerActivity";

    public static final String EXTRA_CRIME_ID = "extra_crime_id";

    private ViewPager mViewPager;

    private ArrayList<Crime> mCrimes;


    /**
     * 获取启动该Activity的Intent
     *
     * @param context
     * @param crimeId
     * @return
     */
    public static Intent getIntent(Context context, UUID crimeId) {
        Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mCrimes = CrimeLab.get(this).getCrimes();
        UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);

        mViewPager = (ViewPager) findViewById(R.id.vp_crime);

        // 使用FragmentStatePagerAdapter来管理多个Fragment，从而可以省略了CrimeActivity。
        mViewPager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Crime crime = mCrimes.get(position);
                return CrimeFragment.newInstance(crime.getId(), position);
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        });

        // 监听都执行了哪些Fragment
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CrimeListFragment.sUpdatePositions.add(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // 定位到当前选择的陋习
        if (crimeId != null) {
            locateCurrentCrime(crimeId);
        } else {
            mViewPager.setCurrentItem(mCrimes.size());
        }
    }

    /**
     * 定位到当前选择的陋习
     * @param crimeId
     */
    private void locateCurrentCrime(UUID crimeId) {
        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }
}
