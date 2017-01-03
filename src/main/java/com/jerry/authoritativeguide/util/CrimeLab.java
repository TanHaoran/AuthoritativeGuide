package com.jerry.authoritativeguide.util;

import android.content.Context;

import com.jerry.authoritativeguide.modle.Crime;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Jerry on 2017/1/3.
 */

public class CrimeLab {

    private static CrimeLab sLab;

    private ArrayList<Crime> mCrimes;

    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0);
            mCrimes.add(crime);
        }
    }

    /**
     * 获取陋习lab单例
     * @param context
     * @return
     */
    public static CrimeLab get(Context context) {
        if (sLab == null) {
            synchronized (CrimeLab.class) {
                if (sLab == null) {
                    sLab = new CrimeLab(context);
                }
            }
        }
        return sLab;
    }

    /**
     * 获取所有陋习集合
     * @return
     */
    public ArrayList<Crime> getCrimes() {
        return mCrimes;
    }

    /**
     * 根据id获取单个陋习
     * @param id
     * @return
     */
    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
            if (id.equals(crime.getId())) {
                return crime;
            }
        }
        return null;
    }

}
