package com.jerry.authoritativeguide.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.PhotoPageFragment;

/**
 * Created by Jerry on 2017/1/19.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    private PhotoPageFragment mPhotoPageFragment;

    public static Intent newIntent(Context context, Uri uri) {
        Intent intent = new Intent(context, PhotoPageActivity.class);
        intent.setData(uri);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        mPhotoPageFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mPhotoPageFragment;
    }

    @Override
    public void onBackPressed() {
        if (mPhotoPageFragment.mWebView.canGoBack()) {
            mPhotoPageFragment.mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
