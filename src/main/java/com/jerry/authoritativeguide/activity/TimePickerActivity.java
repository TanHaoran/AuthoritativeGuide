package com.jerry.authoritativeguide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.TimePickerFragment;

import java.util.Date;

public class TimePickerActivity extends SingleFragmentActivity {

    private static final String EXTRA_DATE = "extra_time";


    public static Intent newIntent(Context context, Date date) {
        Intent intent = new Intent(context, TimePickerActivity.class);
        intent.putExtra(EXTRA_DATE, date);
        return intent;
    }


    @Override
    protected Fragment createFragment() {
        Date date  = (Date) getIntent().getSerializableExtra(EXTRA_DATE);
        return TimePickerFragment.newInstance(date);
    }
}
