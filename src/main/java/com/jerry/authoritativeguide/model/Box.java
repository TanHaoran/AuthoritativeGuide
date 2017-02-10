package com.jerry.authoritativeguide.model;

import android.graphics.PointF;

/**
 * Created by Jerry on 2017/2/4.
 *
 * 保存每一个矩形的数据
 */


public class Box{

    private PointF mOrigin;
    private PointF mCurrent;

    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    public PointF getOrigin() {
        return mOrigin;
    }

    public PointF getCurrent() {
        return mCurrent;
    }

    public void setCurrent(PointF current) {
        mCurrent = current;
    }
}

