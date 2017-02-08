package com.jerry.authoritativeguide.fragment;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jerry.authoritativeguide.R;

/**
 * Created by Jerry on 2017/2/7.
 */

public class MaterialDesignFragment extends Fragment {

    private TextView mElevationTextView;

    private RelativeLayout mRevealLayout;
    private View mRevealPressed;

    private FloatingActionButton mSendButton;

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
        mSendButton = (FloatingActionButton)v.findViewById(R.id.fab_send);

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

        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar sb = Snackbar.make(v, "I'm Snackbar! ", Snackbar.LENGTH_LONG);
                // 添加按钮显示
                sb.setAction("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(), "取消", Toast.LENGTH_SHORT).show();

                    }
                }).show();
                // 设置回调监听
                sb.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        super.onDismissed(transientBottomBar, event);
                    }

                    @Override
                    public void onShown(Snackbar transientBottomBar) {
                        super.onShown(transientBottomBar);
                    }
                });
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
