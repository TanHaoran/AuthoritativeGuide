package com.jerry.authoritativeguide.model;

import com.jerry.authoritativeguide.util.DateUtil;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Jerry on 2016/12/30.
 */

public class Crime {

    private UUID mId;

    private String mTitle;

    private Date mDate;

    private boolean mSolved;

    private String mSuspect;

    private String mSuspectPhone;

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(UUID uuid) {
        mId = uuid;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    /**
     * 获取带格式的日期：yyyy-MM-dd
     *
     * @return
     */
    public String getDateString() {
        return DateUtil.getFormatDateString(mDate, "yyyy-MM-dd") + " " + DateUtil.getWhichDayOfWeek(mDate);
    }

    /**
     * 获取带格式的日期：HH:mm
     *
     * @return
     */
    public String getTimeString() {
        return DateUtil.getFormatDateString(mDate, "HH:mm");
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getSuspectPhone() {
        return mSuspectPhone;
    }

    public void setSuspectPhone(String suspectPhone) {
        mSuspectPhone = suspectPhone;
    }

    /**
     * 获取陋习照片名称
     * @return
     */
    public String getPhotoFileName() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
