package com.jerry.authoritativeguide.fragment;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jerry.authoritativeguide.R;

/**
 * Created by Jerry on 2017/2/6.
 */

public class SunsetFragment extends Fragment {

    private FrameLayout mSkyView;
    private FrameLayout mSeaView;
    private ImageView mSunView;
    private ImageView mSunShadowView;

    private boolean mSunDown;

    private AnimatorSet lightSet;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSkyView = (FrameLayout) v.findViewById(R.id.fl_sky);
        mSeaView = (FrameLayout) v.findViewById(R.id.fl_sea);
        mSunView = (ImageView) v.findViewById(R.id.iv_sun);
        mSunShadowView = (ImageView) v.findViewById(R.id.iv_sun_shadow);

        ObjectAnimator lightXAnimator = ObjectAnimator.ofFloat(mSunView, "scaleX", 1, 0.9f, 1).setDuration(2500);
        lightXAnimator.setRepeatCount(-1);
        ObjectAnimator lightYAnimator = ObjectAnimator.ofFloat(mSunView, "scaleY", 1, 0.9f, 1).setDuration(2500);
        lightYAnimator.setRepeatCount(-1);

        ObjectAnimator lightXShadowAnimator = ObjectAnimator.ofFloat(mSunShadowView, "scaleX", 1, 0.9f, 1).setDuration(2500);
        lightXShadowAnimator.setRepeatCount(-1);
        ObjectAnimator lightYShadowAnimator = ObjectAnimator.ofFloat(mSunShadowView, "scaleY", 1, 0.9f, 1).setDuration(2500);
        lightYShadowAnimator.setRepeatCount(-1);

        lightSet = new AnimatorSet();
        lightSet.play(lightXAnimator).with(lightYAnimator).with(lightXShadowAnimator).with(lightYShadowAnimator);
        lightSet.start();

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });

        return v;
    }

    /**
     * 开始日落动画
     */
    private void startAnimation() {
        int startY = mSunView.getTop();
        int endY = mSkyView.getHeight();

        int startYShadow = mSunShadowView.getTop();
        int endYShadow = -mSunShadowView.getHeight();

        int blueSkyColor = getResources().getColor(R.color.blue_sky);
        int sunsetSkyColor = getResources().getColor(R.color.sunset_sky);
        int nightSkyColor = getResources().getColor(R.color.night_sky);

        // 太阳
        ObjectAnimator sunsetAnimatorDown = ObjectAnimator.ofFloat(mSunView, "y", startY, endY).setDuration(3000);
        sunsetAnimatorDown.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator skyAnimatorDown = ObjectAnimator.ofInt(mSkyView, "backgroundColor", blueSkyColor, sunsetSkyColor).setDuration(3000);
        skyAnimatorDown.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightAnimatorDown = ObjectAnimator.ofInt(mSkyView, "backgroundColor", sunsetSkyColor, nightSkyColor).setDuration(3000);
        nightAnimatorDown.setEvaluator(new ArgbEvaluator());

        ObjectAnimator sunsetAnimatorUp = ObjectAnimator.ofFloat(mSunView, "y", endY, startY).setDuration(3000);
        sunsetAnimatorUp.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator skyAnimatorUp = ObjectAnimator.ofInt(mSkyView, "backgroundColor", sunsetSkyColor, blueSkyColor).setDuration(3000);
        skyAnimatorUp.setEvaluator(new ArgbEvaluator());

        ObjectAnimator nightAnimatorUp = ObjectAnimator.ofInt(mSkyView, "backgroundColor", nightSkyColor, sunsetSkyColor).setDuration(3000);
        nightAnimatorUp.setEvaluator(new ArgbEvaluator());

        // 影子
        ObjectAnimator sunsetShadowAnimatorDown = ObjectAnimator.ofFloat(mSunShadowView, "y", startYShadow, endYShadow).setDuration(3000);
        sunsetShadowAnimatorDown.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator sunsetShadowAnimatorUp = ObjectAnimator.ofFloat(mSunShadowView, "y", endYShadow, startYShadow).setDuration(3000);
        sunsetShadowAnimatorUp.setInterpolator(new AccelerateInterpolator());


        if (!mSunDown) {
            lightSet.cancel();
            AnimatorSet sunDown = new AnimatorSet();
            sunDown.play(sunsetAnimatorDown).with(skyAnimatorDown).with(sunsetShadowAnimatorDown).before(nightAnimatorDown);
            sunDown.start();
        } else {
            AnimatorSet sunUp = new AnimatorSet();
            sunUp.play(sunsetAnimatorUp).with(skyAnimatorUp).with(sunsetShadowAnimatorUp).after(nightAnimatorUp);
            sunUp.start();
            sunUp.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    lightSet.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        mSunDown = !mSunDown;
    }
}
