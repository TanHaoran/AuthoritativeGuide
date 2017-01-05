package com.jerry.authoritativeguide.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.jerry.authoritativeguide.modle.Crime;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Jerry on 2017/1/5.
 */

public class CrimeCursorWrapper extends CursorWrapper {
    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    /**
     * 读取一个陋习
     * @return
     */
    public Crime getCrime() {
        String uuidString = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.DATE));
        int solved = getInt(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SOLVED));
        String suspect = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT));
        String suspectPhone = getString(getColumnIndex(CrimeDbSchema.CrimeTable.Cols.SUSPECT_PHONE));

        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(solved == 1);
        crime.setSuspect(suspect);
        crime.setSuspectPhone(suspectPhone);
        return crime;
    }
}
