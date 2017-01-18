package com.jerry.authoritativeguide.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
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

    private static final int MAX_SOUND = 5;

    private AssetManager mAssets;

    private List<Sound> mSounds = new ArrayList<>();

    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        mAssets = context.getAssets();

        //当前系统的SDK版本大于等于21(Android 5.0)时
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频数量
            builder.setMaxStreams(MAX_SOUND);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build()).build();
            mSoundPool = builder.build();
        }
        //当系统的SDK版本小于21时
        else {
            mSoundPool = new SoundPool(MAX_SOUND, AudioManager.STREAM_SYSTEM, 5);
        }

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
            loadSound(sound);
            mSounds.add(sound);
        }
    }

    /**
     * 加载单个音频
     * @param sound
     */
    private void loadSound(Sound sound) {
        try {
            AssetFileDescriptor descriptor = mAssets.openFd(sound.getAssetPath());
            int id = mSoundPool.load(descriptor, 1);
            sound.setId(id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音频
     * @param sound
     */
    public void play(Sound sound) {
        if (sound.getId() == null) {
            return;
        }
        mSoundPool.play(sound.getId(), 1.0f, 1.0f, 1, 0, 1.0f);
    }

    /**
     * 释放
     */
    public void release() {
        mSoundPool.release();
    }

}
