package com.jerry.authoritativeguide.fragment;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.modle.GalleryItem;
import com.jerry.authoritativeguide.service.PollJobService;
import com.jerry.authoritativeguide.service.PollService;
import com.jerry.authoritativeguide.util.DeviceUtil;
import com.jerry.authoritativeguide.util.FlickrFetchr;
import com.jerry.authoritativeguide.util.QuerySharePreferences;
import com.jerry.authoritativeguide.util.ThumbnailDownloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.jerry.authoritativeguide.util.QuerySharePreferences.getStoredQuery;

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

    private boolean mFinishLayout = false;

    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {

        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);

        Handler handler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(handler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {

            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap bitmap) {
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                target.bindGalleryItem(drawable);
            }
        });

        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "ThumbnailDownloader has started!");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_photo_gallery);
        mProgressBar = (ProgressBar) v.findViewById(R.id.pb_photo_gallery);

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

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mFinishLayout) {
                    mFinishLayout = true;
                    Log.i(TAG, "全局更新");
                    Point point = DeviceUtil.getDevicePoint(getActivity());
                    int columns = point.x / 300;
                    mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), columns));
                }
            }
        });

        // 开始读取照片
        loadGalleryItems();
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        // 设置搜索关键字改变的监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                QuerySharePreferences.setStoredQuery(getActivity(), query);
                // 开始读取照片
                loadGalleryItems();
                //可以收起键盘
                searchView.clearFocus();
                //可以收起SearchView视图
                searchView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i(TAG, "onQueryTextChange :" + newText);
                return false;
            }
        });
        // 设置点击监听
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QuerySharePreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);

        boolean isServiceAlarmOn;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            isServiceAlarmOn = PollService.isServiceAlarmOn(getActivity());
        } else {
            isServiceAlarmOn = PollJobService.isJoBScheduled(getActivity());
        }

        if (isServiceAlarmOn) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QuerySharePreferences.setStoredQuery(getActivity(), null);
                loadGalleryItems();
                return true;
            case R.id.menu_item_toggle_polling:
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    boolean isServiceAlarmOn = !PollService.isServiceAlarmOn(getActivity());
                    PollService.setServiceAlarm(getActivity(), isServiceAlarmOn);
                } else {
                    boolean isServiceAlarmOn = !PollJobService.isJoBScheduled(getActivity());
                    PollJobService.setSchedule(getActivity(), isServiceAlarmOn);
                }
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "ThumbnailDownloader has quit!");
    }

    /**
     * 读取照片
     */
    private void loadGalleryItems() {
        mIsLoading = true;
        Log.i(TAG, "开始读取照片，第" + mCurrentPage + "页");
        String query = getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {

        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {

            FlickrFetchr flickrFetchr = new FlickrFetchr();

            if (mQuery == null) {
                return flickrFetchr.fetchRecentPhotos(mCurrentPage);
            } else {
                return flickrFetchr.searchPhotos(mQuery);
            }
        }

        @Override
        protected void onPostExecute(List<GalleryItem> galleryItems) {
            mIsLoading = false;
            if (mQuery == null) {
                mGalleryItems.addAll(galleryItems);
            } else {
                mGalleryItems = galleryItems;
            }
            setupAdapter();
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mGalleryImageView;

        public PhotoHolder(View itemView) {
            super(itemView);
            mGalleryImageView = (ImageView) itemView;
        }

        public void bindGalleryItem(Drawable drawable) {
            mGalleryImageView.setImageDrawable(drawable);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.item_gallery, parent, false);
            ImageView imageView = (ImageView) v.findViewById(R.id.iv_gallery);
            return new PhotoHolder(imageView);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {

            Picasso.with(getActivity())
                    .load(mGalleryItems.get(position).getUrl_s())
                    .placeholder(R.drawable.bill_up_close)
                    .into(holder.mGalleryImageView);

//            GalleryItem item = mGalleryItems.get(position);
//            Drawable placeholder;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//                placeholder = getResources().getDrawable(R.drawable.bill_up_close, null);
//            } else {
//                placeholder = getResources().getDrawable(R.drawable.bill_up_close);
//            }
//            mThumbnailDownloader.queueThumbnail(holder, item.getUrl_s());
//
//            holder.bindGalleryItem(placeholder);
//            Log.i(TAG, "Got a new url : " + item.getUrl_s());
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
