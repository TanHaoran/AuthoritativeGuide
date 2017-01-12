package com.jerry.authoritativeguide.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.modle.GalleryItem;
import com.jerry.authoritativeguide.util.FlickrFetchr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry on 2017/1/11.
 */

public class PhotoGalleryFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";

    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private PhotoAdapter mPhotoAdapter;

    private List<GalleryItem> mGalleryItems = new ArrayList<>();

    private int mCurrentPage = 1;

    private boolean mIsLoading = false;

    public static PhotoGalleryFragment newInstance() {

        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_photo_gallery);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pb_photo_gallery);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (mIsLoading) {
                    return;
                }

                if (!mRecyclerView.canScrollVertically(1)) {
                    Log.i(TAG, "滑动到底布啦");
                    mCurrentPage++;
                    loadGalleryItems();
                }
            }

        });
        // 开始读取照片
        loadGalleryItems();
        return v;
    }

    /**
     * 读取照片
     */
    private void loadGalleryItems() {
        mIsLoading = true;
        Log.i(TAG, "开始读取照片，第" + mCurrentPage + "页");
        new FetchItemsTask().execute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            return new FlickrFetchr().fetchItems(mCurrentPage);
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mIsLoading = false;
            mGalleryItems.addAll(galleryItems);
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private TextView mTitleTextView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mTitleTextView = (TextView) itemView;
        }

        public void bindGalleryItem(GalleryItem item) {
            mTitleTextView.setText(item.getTitle());
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView = new TextView(getActivity());
            return new PhotoHolder(textView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            holder.bindGalleryItem(mGalleryItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        public void setGalleryItems(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }
    }


    private void setupAdapter() {
        if (isAdded()) {
            if (mPhotoAdapter == null) {
                mPhotoAdapter = new PhotoAdapter(mGalleryItems);
                mRecyclerView.setAdapter(mPhotoAdapter);
            } else {
                mPhotoAdapter.setGalleryItems(mGalleryItems);
                mPhotoAdapter.notifyDataSetChanged();
            }
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
