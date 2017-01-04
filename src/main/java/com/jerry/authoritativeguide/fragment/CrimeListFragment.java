package com.jerry.authoritativeguide.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    private static final String EXTRA_SUBTITLE = "extra_subtitle";

    private RecyclerView mRecyclerView;
    private CrimeAdapter mAdapter;

    private boolean mSubtitleVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(EXTRA_SUBTITLE);
        }

        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        // 通知Fragment有菜单选择项
        setHasOptionsMenu(true);

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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem item = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            item.setTitle(R.string.hide_subtitle);
        } else {
            item.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).add(crime);

                Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
               return  super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_SUBTITLE, mSubtitleVisible);
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
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    /**
     * 更新工具栏的副标题
     */
    private void updateSubtitle() {
        int size = CrimeLab.get(getActivity()).getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, new Integer(size));
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
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
            Intent intent = CrimePagerActivity.getIntent(getActivity(), mCrime.getId());
            startActivity(intent);
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
