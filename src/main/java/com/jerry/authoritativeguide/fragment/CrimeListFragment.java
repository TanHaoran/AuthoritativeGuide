package com.jerry.authoritativeguide.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.activity.CrimePagerActivity;
import com.jerry.authoritativeguide.modle.Crime;
import com.jerry.authoritativeguide.util.CrimeLab;

import java.util.ArrayList;

/**
 * Created by Jerry on 2017/1/3.
 */
public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME = 1;

    private RecyclerView mRecyclerView;
    private CrimeAdapter mAdapter;

    /**
     * 需要更新的陋习位置
     */
    private int mUpdatePosition = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_crime);
        // 如果不设置布局管理器，就会报错无法运行
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CRIME && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mUpdatePosition = data.getIntExtra(CrimePagerActivity.EXTRA_POSITION, 0);
            }
        }
    }

    /**
     * 更新界面UI
     */
    private void updateUI() {
        // 如果是第一次应该创建适配器，其它他情况应该更新界面
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(CrimeLab.get(getActivity()).getCrimes());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            // 只更新需要更新的那一个数据
            mAdapter.notifyItemChanged(mUpdatePosition);
        }
    }

    /**
     * 界面持有者
     */
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Crime mCrime;

        public TextView mTitleTextView;
        public TextView mDateTextView;
        public CheckBox mSolvedCheckBox;

        public CrimeHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView.findViewById(R.id.tv_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.tv_date);
            mSolvedCheckBox = (CheckBox) itemView.findViewById(R.id.cb_solved);
            itemView.setOnClickListener(this);
        }

        /**
         * 将陋习绑定到ViewHolder上
         *
         * @param crime
         */
        public void bindCrime(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDateString());
            mSolvedCheckBox.setChecked(mCrime.isSolved());
        }

        /**
         * item点击事件
         *
         * @param v
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = CrimePagerActivity.getIntent(getActivity(), mCrime.getId(), position);
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }

    /**
     * 设配器
     */
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private ArrayList<Crime> mCrimes;

        public CrimeAdapter(ArrayList<Crime> crimes) {
            mCrimes = crimes;
        }

        /**
         * 创建一个ViewHolder
         *
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(v);
        }

        /**
         * 将该位置的陋习绑定到所对应的ViewHolder上
         *
         * @param holder
         * @param position
         */
        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bindCrime(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }
}
