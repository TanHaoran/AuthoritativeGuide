package com.jerry.authoritativeguide.activity;

import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.PhotoGalleryFragment;

/**
 * Created by Jerry on 2017/1/11.
 */
public class PhotoGalleryActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
