package com.jerry.authoritativeguide.util;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Jerry on 2017/1/17.
 */

public class QuerySharePreferences {

    private static final String PRE_SEARCH_QUERY = "searchQuery";
    private static final String PRE_LAST_RESULT_ID = "lastResultId";
    private static final String PRE_IS_ALARM_ON = "isAlarmOn";

    public static String getStoredQuery(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PRE_SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context, String query) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PRE_SEARCH_QUERY, query).apply();
    }

    public static String getLastResultId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PRE_LAST_RESULT_ID, null);
    }

    public static void setLastResultId(Context context, String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PRE_LAST_RESULT_ID, lastResultId).apply();
    }

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PRE_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isAlarmOn) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PRE_IS_ALARM_ON, isAlarmOn).apply();
    }
}
