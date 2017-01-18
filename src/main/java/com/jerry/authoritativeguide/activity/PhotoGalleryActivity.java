package com.jerry.authoritativeguide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.PhotoGalleryFragment;

/**
 * Created by Jerry on 2017/1/11.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity{

    public static Intent newIntent(Context context) {
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
