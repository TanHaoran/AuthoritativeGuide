package com.jerry.authoritativeguide.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.jerry.authoritativeguide.R;
import com.jerry.authoritativeguide.util.ActivityCollector;
import com.jerry.authoritativeguide.util.PictureUtil;

/**
 * Created by Jerry on 2017/1/9.
 */

public class PhotoFragment extends DialogFragment {

    private static final String ARGS_PHOTO_PATH= "args_photo_path";

    private ImageView mPhotoImageView;

    private Button mOkButton;

    public static PhotoFragment newInstance(String path) {

        Bundle args = new Bundle();

        args.putSerializable(ARGS_PHOTO_PATH, path);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String path = (String) getArguments().getSerializable(ARGS_PHOTO_PATH);

        View v = inflater.inflate(R.layout.dialog_photo, container, false);

        mPhotoImageView = (ImageView) v.findViewById(R.id.iv_photo);
        mOkButton = (Button) v.findViewById(R.id.btn_ok);

        Bitmap bitmap = PictureUtil.getScaleBitmap(path, ActivityCollector.getTop());
        mPhotoImageView.setImageBitmap(bitmap);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTargetFragment() != null) {
                    dismiss();
                } else {
                    getActivity().finish();
                }
            }
        });
        return v;
    }


}
