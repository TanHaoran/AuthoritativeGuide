package com.jerry.authoritativeguide.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.jerry.authoritativeguide.modle.Sound;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry on 2017/1/9.
 */

public class BeatBox {

    private static final String TAG = "BeatBox";
    private static final String SOUND_FOLDER = "sample_sounds";

    private AssetManager mAssets;

    private List<Sound> mSounds = new ArrayList<>();

    public BeatBox(Context context) {
        mAssets = context.getAssets();
        loadSounds();
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    private void loadSounds() {
        String[] soundsNames = null;
        try {
            soundsNames = mAssets.list(SOUND_FOLDER);
            Log.i(TAG, "Load " + soundsNames.length + " sounds.");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "Load failed!");
        }
        for (String name : soundsNames) {
            String assetPath = SOUND_FOLDER + "/" + name;
            Sound sound = new Sound(assetPath);
            mSounds.add(sound);
        }
    }
}
