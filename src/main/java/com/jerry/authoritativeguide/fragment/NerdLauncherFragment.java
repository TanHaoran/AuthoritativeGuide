package com.jerry.authoritativeguide.fragment;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jerry.authoritativeguide.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Jerry on 2017/1/10.
 */
public class NerdLauncherFragment extends Fragment {


    private RecyclerView mRecyclerView;

    public static NerdLauncherFragment newInstance() {

        return new NerdLauncherFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_nerd_launcher);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setUpAdapter();

        return v;
    }

    /**
     * 设置RecyclerView的适配器
     */
    private void setUpAdapter() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        final PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);

        // 对符合启动的Activity进行排序
        Collections.sort(resolveInfos, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo o1, ResolveInfo o2) {
                return String.CASE_INSENSITIVE_ORDER.compare(o1.loadLabel(pm).toString(),
                        o2.loadLabel(pm).toString());
            }
        });

        mRecyclerView.setAdapter(new ActivityAdapter(resolveInfos));
    }

    private class ActivityHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ResolveInfo mResolveInfo;
        private ImageView mLaunchImageView;
        private TextView mNameTextView;

        public ActivityHolder(View itemView) {
            super(itemView);
            mLaunchImageView = (ImageView) itemView.findViewById(R.id.iv_activity);
            mNameTextView = (TextView) itemView.findViewById(R.id.tv_activity);
            itemView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();

            mLaunchImageView.setImageDrawable(mResolveInfo.loadIcon(pm));
            mNameTextView.setText(mResolveInfo.loadLabel(pm).toString());
        }

        @Override
        public void onClick(View v) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_MAIN);
            intent.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {

        private List<ResolveInfo> mResolveInfos;

        public ActivityAdapter(List<ResolveInfo> resolveInfos) {
            mResolveInfos = resolveInfos;
        }

        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_list_activity, parent, false);
            return new ActivityHolder(v);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            holder.bindActivity(mResolveInfos.get(position));
        }

        @Override
        public int getItemCount() {
            return mResolveInfos.size();
        }
    }
}
