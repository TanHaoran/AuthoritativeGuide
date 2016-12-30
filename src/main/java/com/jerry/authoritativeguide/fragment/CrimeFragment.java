package com.jerry.authoritativeguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.modle.Crime;
import com.jerry.authoritativeguide.util.DateUtil;

import java.util.Date;

/**
 * Created by Jerry on 2016/12/30.
 */

public class CrimeFragment extends Fragment {

    private Crime mCrime;

    private EditText mTitleField;
    private Button mDateField;
    private CheckBox mSolvedCheckBox;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCrime = new Crime();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.et_title);
        mDateField = (Button) v.findViewById(R.id.btn_date);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.cb_solved);

        // 设置标题
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 设置日期
        Date date = mCrime.getDate();
        mDateField.setText(DateUtil.getFormatDateString(date, "yyyy-mm-dd") +
                " " + DateUtil.getWhichDayOfWeek(date));
        mDateField.setEnabled(false);

        // 设置是否解决
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSolvedCheckBox.setChecked(isChecked);
            }
        });

        return v;
    }
}
