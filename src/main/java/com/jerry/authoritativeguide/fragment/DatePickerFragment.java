package com.jerry.authoritativeguide.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.jerry.authoritativeguide.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Jerry on 2017/1/4.
 */

public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE = "extra_date";

    public static final String ARGS_DATE = "args_date";

    private DatePicker mDatePicker;
    private Button mOkButton;

    private Calendar mCalendar;

    public static DatePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARGS_DATE, date);

        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) view.findViewById(R.id.dp_date);
        mOkButton = (Button) view.findViewById(R.id.btn_ok);

        // 将从CrimeFragment传过来日期设置到界面上
        Date date = (Date) getArguments().getSerializable(ARGS_DATE);
        mCalendar = Calendar.getInstance();
        mCalendar.setTime(date);

        // 获取年月日的数据
        int year = mCalendar.get(Calendar.YEAR);
        int month = mCalendar.get(Calendar.MONTH);
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);
        // 日期变动监听没有使用，所以传null就可以了
        mDatePicker.init(year, month, day, null);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = mDatePicker.getYear();
                int month = mDatePicker.getMonth();
                int day = mDatePicker.getDayOfMonth();
                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                int minute = mCalendar.get(Calendar.MINUTE);
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
        // 如果目标Fragment不存在，就调用Fragment之间传递参数的方式；
        // 如果目标Fragment存在说明是托管的Activity中的，那么就调用Activity之间的传递参数。
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
