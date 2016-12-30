package com.jerry.authoritativeguide.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jerry on 2016/12/30.
 */

public class DateUtil {

    /**
     * 将日期转换成指定格式
     *
     * @param date
     * @param formatString
     * @return
     */
    public static String getFormatDateString(Date date, String formatString) {
        SimpleDateFormat format = new SimpleDateFormat(formatString);
        return format.format(date);
    }

    /**
     * 获取日期对象是星期几
     * @param date
     * @return
     */
    public static String getWhichDayOfWeek(Date date) {
        String[] daysOfWeek = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int index = calendar.get(Calendar.DAY_OF_WEEK);
        return daysOfWeek[index - 1];
    }
}
