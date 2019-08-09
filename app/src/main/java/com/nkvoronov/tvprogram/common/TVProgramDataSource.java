package com.nkvoronov.tvprogram.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.nkvoronov.tvprogram.database.ConfigCursorWrapper;
import com.nkvoronov.tvprogram.database.TVProgramBaseHelper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ConfigsTable;

public class TVProgramDataSource {
    private static final String DEF_ID = "1";
    public static final String TAG = "TVPROGRAM";

    private static TVProgramDataSource sTVProgramLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private int mCoutDays;
    private String mLang;
    private boolean mIndexSort;

    public TVProgramDataSource(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TVProgramBaseHelper(mContext).getWritableDatabase();
        initConfig();
    }

    private void initConfig() {
        mCoutDays = 7;
        mLang = "ru";
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
                mLang = cursor.getLang();
                mIndexSort = cursor.getIndexSort();
                Log.d(TAG, "initConfig get");
            }
        } finally {
            cursor.close();
        }

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
        values.put(ConfigsTable.Cols.LANG, getLang());
        values.put(ConfigsTable.Cols.INDEX_SORT, isIndexSort() ? 1 : 0);
        return values;
    }

    public static TVProgramDataSource get(Context context) {
        if (sTVProgramLab == null) {
            sTVProgramLab = new TVProgramDataSource(context);
        }
        return sTVProgramLab;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public int getCoutDays() {
        return mCoutDays;
    }

    public void setCoutDays(int coutDays) {
        mCoutDays = coutDays;
        updateConfig();
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        mLang = lang;
        updateConfig();
    }

    public boolean isIndexSort() {
        return mIndexSort;
    }

    public void setIndexSort(boolean indexSort) {
        mIndexSort = indexSort;
        updateConfig();
    }
}
