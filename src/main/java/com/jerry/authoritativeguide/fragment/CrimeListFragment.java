package com.jerry.authoritativeguide.fragment;

import android.content.Context;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.modle.Crime;
import com.jerry.authoritativeguide.util.CrimeLab;

import java.util.ArrayList;

/**
 * Created by Jerry on 2017/1/3.
 */
public class CrimeListFragment extends Fragment {

    public interface CallBack {
        /**
         * 选中一个或者新增一个Crime时，更新列表界面
         * @param crime
         */
        void onCrimeSelected(Crime crime);
    }

    private static final String EXTRA_SUBTITLE = "extra_subtitle";

    private RecyclerView mRecyclerView;
    private CrimeAdapter mAdapter;

    private TextView mEmptyMsgTextView;
    private Button mAddOneButton;

    private boolean mSubtitleVisible;

    private CallBack mCallBack;


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
        mEmptyMsgTextView = (TextView) v.findViewById(R.id.tv_empty_msg);
        mAddOneButton = (Button) v.findViewById(R.id.btn_add_one_immediately);

        // 如果不设置布局管理器，就会报错无法运行
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 添加按钮响应事件
        mAddOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newCrime();
            }
        });

        updateUI();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // 更新UI
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
                newCrime();
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_SUBTITLE, mSubtitleVisible);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallBack = (CallBack) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    /**
     * 更新界面UI
     */
    public void updateUI() {
        ArrayList<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();

        // 如果是第一次应该创建适配器，其它他情况应该更新界面
        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }

        if (CrimeLab.get(getActivity()).getCrimes().isEmpty()) {
            mEmptyMsgTextView.setVisibility(View.VISIBLE);
            mAddOneButton.setVisibility(View.VISIBLE);
        } else {
            mEmptyMsgTextView.setVisibility(View.INVISIBLE);
            mAddOneButton.setVisibility(View.INVISIBLE);
        }
        updateSubtitle();
    }

    /**
     * 更新工具栏的副标题
     */
    private void updateSubtitle() {
        int size = CrimeLab.get(getActivity()).getCrimes().size();
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_plural,
                new Integer(size), new Integer(size));
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        if (!mSubtitleVisible) {
            subtitle = null;
        }
        appCompatActivity.getSupportActionBar().setSubtitle(subtitle);
    }


    /**
     * 添加一个新陋习
     */
    private void newCrime() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).add(crime);

        mCallBack.onCrimeSelected(crime);
        // 这里要立刻刷新，因为在平板端list列表左侧不会立刻刷新
        updateUI();
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
            mCallBack.onCrimeSelected(mCrime);
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
            View v = inflater.inflate(R.layout.item_list_crime, parent, false);
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

        public void setCrimes(ArrayList<Crime> crimes) {
            mCrimes = crimes;
        }
    }
}
