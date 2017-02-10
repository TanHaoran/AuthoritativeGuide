package com.jerry.authoritativeguide.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.jerry.authoritativeguide.database.CrimeBaseHelper;
import com.jerry.authoritativeguide.database.CrimeCursorWrapper;
import com.jerry.authoritativeguide.database.CrimeDbSchema;
import com.jerry.authoritativeguide.model.Crime;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Jerry on 2017/1/3.
 */

public class CrimeLab {

    private static CrimeLab sLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        CrimeBaseHelper crimeBaseHelper = new CrimeBaseHelper(mContext);
        mDatabase = crimeBaseHelper.getWritableDatabase();
    }

    /**
     * 获取陋习lab单例
     *
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
     * 添加一项陋习
     *
     * @param crime
     */
    public void add(Crime crime) {
        ContentValues contentValues = getContentValues(crime);
        mDatabase.insert(CrimeDbSchema.CrimeTable.NAME, null, contentValues);
    }

    /**
     * 删除一项陋习
     *
     * @param crime
     */
    public void delete(Crime crime) {
        mDatabase.delete(CrimeDbSchema.CrimeTable.NAME, CrimeDbSchema.CrimeTable.Cols.UUID + "=?",
                new String[]{crime.getId().toString()});
    }

    /**
     * 修改一条陋习
     *
     * @param crime
     */
    public void update(Crime crime) {
        ContentValues contentValues = getContentValues(crime);
        mDatabase.update(CrimeDbSchema.CrimeTable.NAME, contentValues, CrimeDbSchema.CrimeTable.Cols.UUID + "=?",
                new String[]{crime.getId().toString()});
    }

    /**
     * 从数据库中读取陋习集合
     *
     * @return
     */
    public ArrayList<Crime> getCrimes() {
        ArrayList<Crime> crimes = new ArrayList<>();

        CrimeCursorWrapper cursorWrapper = queryCrimes(null, null);

        try {
            cursorWrapper.moveToFirst();
            Crime crime;
            while (!cursorWrapper.isAfterLast()) {
                crime = cursorWrapper.getCrime();
                crimes.add(crime);
                cursorWrapper.moveToNext();
            }
        } finally {
            cursorWrapper.close();
        }
        return crimes;
    }

    /**
     * 根据id获取单个陋习
     *
     * @param id
     * @return
     */
    public Crime getCrime(UUID id) {
        CrimeCursorWrapper cursorWrapper = queryCrimes(CrimeDbSchema.CrimeTable.Cols.UUID + "=?",
                new String[]{id.toString()});

        try {
            if (cursorWrapper.getCount() == 0) {
                return null;
            } else {
                cursorWrapper.moveToFirst();
                return cursorWrapper.getCrime();
            }
        } finally {
            cursorWrapper.close();
        }
    }

    /**
     * 获取查询结果的CursorWrapper对象
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(CrimeDbSchema.CrimeTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new CrimeCursorWrapper(cursor);
    }

    /**
     * 将陋习组装成ContentValues
     *
     * @param crime
     * @return
     */
    private ContentValues getContentValues(Crime crime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.UUID, crime.getId().toString());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.TITLE, crime.getTitle());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.DATE, crime.getDate().getTime());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT, crime.getSuspect());
        contentValues.put(CrimeDbSchema.CrimeTable.Cols.SUSPECT_PHONE, crime.getSuspectPhone());
        return contentValues;
    }

    /**
     * 获取陋习照片文件
     * @param crime
     * @return
     */
    public File getPhotoFile(Crime crime) {
        File externalFilesDir =  mContext.getExternalFilesDir(Environment
                .DIRECTORY_PICTURES);
        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, crime.getPhotoFileName());
    }
}



