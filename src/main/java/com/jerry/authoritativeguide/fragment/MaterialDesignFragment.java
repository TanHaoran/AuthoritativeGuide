package com.jerry.authoritativeguide.fragment;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.authoritativeguide.R;

/**
 * Created by Jerry on 2017/2/7.
 */

public class MaterialDesignFragment extends Fragment {

    private TextView mElevationTextView;

    private RelativeLayout mRevealLayout;
    private View mRevealPressed;

    public static MaterialDesignFragment newInstance() {
        return new MaterialDesignFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_material_design, container, false);

        mElevationTextView = (TextView) v.findViewById(R.id.tv_10elevation);
        mRevealLayout = (RelativeLayout) v.findViewById(R.id.rl_reveal);
        mRevealPressed = v.findViewById(R.id.v_reveal_pressed);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mElevationTextView.setElevation(10f);
        }


        mRevealLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        int x = (int) event.getX();
                        int y = (int) event.getY();
                        startAnimation(x, y);
                        break;
                }
                return true;
            }
        });

        return v;
    }


    /**
     * 创建按钮点击动画
     */
    private void startAnimation(int x, int y) {

        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int maxRadius = size.y;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ViewAnimationUtils.createCircularReveal(mRevealPressed, x, y, 0, maxRadius).setDuration(1000).start();
        }
    }
}
