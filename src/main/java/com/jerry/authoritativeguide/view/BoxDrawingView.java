package com.jerry.authoritativeguide.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.jerry.authoritativeguide.modle.Box;

import java.util.ArrayList;

/**
 * Created by Jerry on 2017/2/4.
 */

public class BoxDrawingView extends View {

    private static final String TAG = "BoxDrawingView";

    private Box mCurrentBox;
    private ArrayList<Box> mBoxes = new ArrayList<>();

    private Paint mBackgroundPaint;
    private Paint mRectPaint;

    enum MODE {
        NULL, SINGLE, MULTIPLE
    }

    private MODE mMODE = MODE.NULL;

    /**
     * 初始的角度
     */
    private float mStartRotation;
    /**
     * 旋转后的角度
     */
    private float mEndRotation;

    private float mChangeRotation;

    public BoxDrawingView(Context context) {
        this(context, null);
    }

    public BoxDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xFFFFFFFF);

        mRectPaint = new Paint();
        mRectPaint.setColor(0xFFFF0000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF pointF = new PointF(event.getX(), event.getY());

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mMODE = MODE.SINGLE;
                mCurrentBox = new Box(pointF);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mMODE = MODE.MULTIPLE;
                // 两个手指落下去，获取初始角度
                mStartRotation = getRotation(event);
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointF != null) {
                    if (mMODE == MODE.MULTIPLE) {
                        mEndRotation = getRotation(event);
                    } else {
                        mCurrentBox.setCurrent(pointF);
                    }
                    // 这里就会执行onDraw方法
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mMODE = MODE.NULL;
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mMODE = MODE.SINGLE;
                break;
            case MotionEvent.ACTION_CANCEL:
                mMODE = MODE.NULL;
                mCurrentBox = null;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

        mChangeRotation = mEndRotation - mStartRotation;
        canvas.rotate(mChangeRotation, getWidth() / 2, getHeight() / 2);

        for (Box box : mBoxes) {
            // 取得所有经过的点的最左、上、右、下
            float left = Math.min(box.getOrigin().x, box.getCurrent().x);
            float top = Math.min(box.getOrigin().y, box.getCurrent().y);
            float right = Math.max(box.getOrigin().x, box.getCurrent().x);
            float down = Math.max(box.getOrigin().y, box.getCurrent().y);
            canvas.drawRect(left, top, right, down, mRectPaint);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superData = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putParcelable("super_data", superData);
        ArrayList list = new ArrayList();
        list.add(mBoxes);
        bundle.putParcelableArrayList("boxes", list);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        Parcelable superData = bundle.getParcelable("super_data");
        ArrayList list = bundle.getParcelableArrayList("boxes");
        mBoxes = (ArrayList<Box>) list.get(0);
        super.onRestoreInstanceState(superData);
    }

    /**
     * @param event
     * @return 获取旋转角度
     */
    private float getRotation(MotionEvent event) {
        double delta_x = (event.getX(0) - event.getX(1));
        double delta_y = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(delta_y, delta_x);
        return (float) Math.toDegrees(radians);
    }
}
