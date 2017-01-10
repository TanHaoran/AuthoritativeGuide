package com.jerry.authoritativeguide.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.modle.Sound;
import com.jerry.authoritativeguide.util.BeatBox;

import java.util.List;

/**
 * Created by Jerry on 2017/1/9.
 */
public class BeatBoxFragment extends Fragment {

    private RecyclerView mRecyclerView;

    private BeatBox mBeatBox;

    public static BeatBoxFragment newInstance() {

        BeatBoxFragment fragment = new BeatBoxFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        mBeatBox = new BeatBox(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_beat_box, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.rv_beat_box);
        // 设置一个3列的布局管理器
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mRecyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));


        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }

    private class SoundHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button mSoundButton;

        private Sound mSound;

        public SoundHolder(LayoutInflater inflater, @Nullable ViewGroup container) {
            super(inflater.inflate(R.layout.item_list_sound, container, false));
            mSoundButton = (Button) itemView.findViewById(R.id.btn_sound);
            mSoundButton.setOnClickListener(this);
        }

        public void bindSound(Sound sound) {
            mSound = sound;
            mSoundButton.setText(sound.getName());
        }

        @Override
        public void onClick(View v) {
            mBeatBox.play(mSound);
        }
    }


    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {

        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @Override
        public SoundHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SoundHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(SoundHolder holder, int position) {
            holder.bindSound(mSounds.get(position));
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }
}
