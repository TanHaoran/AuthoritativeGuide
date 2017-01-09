package com.jerry.authoritativeguide.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jerry on 2017/1/5.
 */

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "crimeBase.db";
    private static final int VERSION = 1;

    public CrimeBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + CrimeDbSchema.CrimeTable.NAME +
                "(_id integer primary key autoincrement, " + CrimeDbSchema.CrimeTable.Cols.UUID +
                ", " + CrimeDbSchema.CrimeTable.Cols.TITLE + ", " + CrimeDbSchema.CrimeTable.Cols.DATE +
                ", " + CrimeDbSchema.CrimeTable.Cols.SOLVED + ", " + CrimeDbSchema.CrimeTable.Cols.SUSPECT +
                ", " + CrimeDbSchema.CrimeTable.Cols.SUSPECT_PHONE + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}