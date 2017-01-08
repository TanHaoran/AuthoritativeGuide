package com.jerry.authoritativeguide.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jerry on 2017/1/6.
 */

public class ActivityCollector {


    private static List<Activity> mActivities = new ArrayList<>();

    /**
     * 添加Activity到集合中
     *
     * @param activity
     */
    public static void add(Activity activity) {
        mActivities.add(activity);
    }

    /**
     * 移除一个Activity
     *
     * @param activity
     */
    public static void remove(Activity activity) {
        mActivities.remove(activity);
    }

    /**
     * 获取栈顶的Activity
     * @return
     */
    public static Activity getTop() {
        return mActivities.get(mActivities.size() - 1);
    }
}
