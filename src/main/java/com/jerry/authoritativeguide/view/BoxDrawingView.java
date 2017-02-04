package com.jerry.authoritativeguide.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.jerry.authoritativeguide.modle.Box;

import java.util.ArrayList;

/**
 * Created by Jerry on 2017/2/4.
 */

public class BoxDrawingView extends View {

    private Box mCurrentBox;
    private ArrayList<Box> mBoxes = new ArrayList<>();

    private Paint mBackgroundPaint;
    private Paint mRectPaint;

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

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mCurrentBox = new Box(pointF);
                mBoxes.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointF != null) {
                    mCurrentBox.setCurrent(pointF);
                    // 这里就会执行onDraw方法
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                mCurrentBox = null;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPaint(mBackgroundPaint);

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
}
