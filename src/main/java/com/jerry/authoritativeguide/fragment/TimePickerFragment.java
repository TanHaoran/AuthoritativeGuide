package com.jerry.authoritativeguide.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;

import com.jerry.authoritativeguide.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.jerry.authoritativeguide.fragment.DatePickerFragment.ARGS_DATE;
import static com.jerry.authoritativeguide.fragment.DatePickerFragment.EXTRA_DATE;


/**
 * Created by Jerry on 2017/1/4.
 */

@SuppressWarnings("deprecation")
public class TimePickerFragment extends DialogFragment {

    private TimePicker mTimePicker;

    private Button mOkButton;

    private Calendar mCalendar;

    public static TimePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_DATE, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) view.findViewById(R.id.tp_time);
        mOkButton = (Button) view.findViewById(R.id.btn_ok);

        // 将从CrimeFragment传过来日期设置到界面上
        Date date = (Date) getArguments().getSerializable(ARGS_DATE);

        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);

        // 获取时分的数据
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = mCalendar.get(Calendar.MINUTE);
        // 日期变动监听没有使用，所以传null就可以了
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mCalendar.get(Calendar.YEAR);
                int month = mCalendar.get(Calendar.MONTH);
                int day = mCalendar.get(Calendar.DAY_OF_MONTH);
                int hour, minute;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = mTimePicker.getHour();
                    minute = mTimePicker.getMinute();
                } else {
                    hour = mTimePicker.getCurrentHour();
                    minute = mTimePicker.getCurrentMinute();
                }

                Date date = new GregorianCalendar(year, month, day, hour, minute).getTime();
                // 设置返回的日期数据
                setResult(Activity.RESULT_OK, date);
            }
        });
        return view;
    }

    /**
     * 设置返回的日期数据
     *
     * @param resultCode
     * @param date
     */
    private void setResult(int resultCode, Date date) {
        Intent data = new Intent();
        data.putExtra(EXTRA_DATE, date);
        if (getTargetFragment() != null) {
            Fragment fragment = getTargetFragment();
            fragment.onActivityResult(getTargetRequestCode(), resultCode, data);
            dismiss();
        } else {
            getActivity().setResult(resultCode, data);
            getActivity().finish();
        }
    }
}
