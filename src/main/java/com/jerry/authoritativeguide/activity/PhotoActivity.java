package com.jerry.authoritativeguide.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.jerry.authoritativeguide.fragment.PhotoFragment;

/**
 * Created by Jerry on 2017/1/9.
 */

public class PhotoActivity extends SingleFragmentActivity {

    private static final String EXTRA_PHOTO_PATH = "extra_photo_path";


    /**
     * 获取导向照片Activity的Intent
     *
     * @param context
     * @param path
     * @return
     */
    public static Intent getIntent(Context context, String path) {
        Intent intent = new Intent(context, PhotoActivity.class);
        intent.putExtra(EXTRA_PHOTO_PATH, path);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        String path = getIntent().getStringExtra(EXTRA_PHOTO_PATH);
        return PhotoFragment.newInstance(path);
    }
}
