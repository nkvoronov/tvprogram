package com.nkvoronov.tvprogram.common;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.nkvoronov.tvprogram.database.ConfigCursorWrapper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class AppConfig {
    private static final String DEF_ID = "1";

    private int mCoutDays;
    private boolean mIndexSort;
    private SQLiteDatabase mDatabase;

    public AppConfig(SQLiteDatabase database) {
        mDatabase = database;
        initConfig();
    }

    private void initConfig() {
        mCoutDays = 7;
        mIndexSort = true;
        Log.d(TAG, "initConfig");
        ConfigCursorWrapper cursor = new ConfigCursorWrapper(mDatabase.query(
                ConfigsTable.TABLE_NAME,
                null,
                ConfigsTable.Cols.ID + " = ?",
                new String[]{ DEF_ID },
                null,
                null,
                null
        ));
        try {
            if (cursor.getCount() == 0) {
                mDatabase.insert(ConfigsTable.TABLE_NAME, null, getConfigValues(true));
                Log.d(TAG, "initConfig insert");
            } else {
                cursor.moveToFirst();
                mCoutDays = cursor.getCountDays();
                mIndexSort = cursor.getIndexSort();
                Log.d(TAG, "initConfig get");
            }
        } finally {
            cursor.close();
        }

    }

    public int getCoutDays() {
        return mCoutDays;
    }

    public void setCoutDays(int coutDays) {
        mCoutDays = coutDays;
        updateConfig();
    }

    public boolean isIndexSort() {
        return mIndexSort;
    }

    public void setIndexSort(boolean indexSort) {
        mIndexSort =indexSort;
        updateConfig();
    }

    private void updateConfig() {
        mDatabase.update(ConfigsTable.TABLE_NAME, getConfigValues(false), ConfigsTable.Cols.ID + " = ?", new String[]{ DEF_ID });
    }

    private ContentValues getConfigValues(boolean withID) {
        ContentValues values = new ContentValues();
        if (withID) {
            values.put(ConfigsTable.Cols.ID, DEF_ID);
        }
        values.put(ConfigsTable.Cols.COUNT_DAYS, getCoutDays());
        values.put(ConfigsTable.Cols.INDEX_SORT, isIndexSort() ? 1 : 0);
        return values;
    }
}
