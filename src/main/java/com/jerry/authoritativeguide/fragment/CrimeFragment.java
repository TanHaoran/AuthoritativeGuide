package com.jerry.authoritativeguide.fragment;

import android.app.Activity;
import android.content.Intent;
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
import com.jerry.authoritativeguide.activity.CrimePagerActivity;
import com.jerry.authoritativeguide.modle.Crime;
import com.jerry.authoritativeguide.util.CrimeLab;

import java.util.UUID;

/**
 * Created by Jerry on 2016/12/30.
 */

public class CrimeFragment extends Fragment {

    private static final String ARGS_CRIME_ID = "args_crime_id";
    private static final String ARGS_POSITION = "args_crime_position";

    private Crime mCrime;

    private EditText mTitleEditText;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    /**
     * 通过陋习id创建一个自己的实例
     *
     * @param crimeId
     * @return
     */
    public static Fragment newInstance(UUID crimeId, int position) {
        // 保存陋习id
        Bundle args = new Bundle();
        args.putSerializable(ARGS_CRIME_ID, crimeId);
        args.putInt(ARGS_POSITION, position);

        // 创建实例
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 这里通过Arguments来获取陋习id，从而脱离的activity的限制
        UUID crimeId = (UUID) getArguments().getSerializable(ARGS_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        setResult();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleEditText = (EditText) v.findViewById(R.id.et_title);
        mDateButton = (Button) v.findViewById(R.id.btn_date);
        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.cb_solved);

        // 设置标题
        mTitleEditText.setText(mCrime.getTitle());
        mTitleEditText.addTextChangedListener(new TextWatcher() {
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
        mDateButton.setText(mCrime.getDateString());
        mDateButton.setEnabled(false);

        // 设置是否解决
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSolvedCheckBox.setChecked(isChecked);
            }
        });


        return v;
    }


    /**
     * 设置返回的数据
     */
    private void setResult() {
        Intent data = new Intent();
        data.putExtra(CrimePagerActivity.EXTRA_POSITION, getArguments().getInt(ARGS_POSITION, -1));
        getActivity().setResult(Activity.RESULT_OK, data);
    }

}
