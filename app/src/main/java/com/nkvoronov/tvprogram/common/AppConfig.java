package com.nkvoronov.tvprogram.common;

import android.content.ContentValues;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import com.nkvoronov.tvprogram.database.ConfigCursorWrapper;

public class AppConfig {
    private static final String DEF_ID = "1";

    private int mCoutDays;
    private boolean mFullDesc;
    MainDataSource mDataSource;

    public AppConfig(MainDataSource dataSource) {
        mDataSource = dataSource;
        initConfig();
    }

    private void initConfig() {
        mCoutDays = 7;
        mFullDesc = false;
        ConfigCursorWrapper cursor = new ConfigCursorWrapper(mDataSource.getDatabase().query(
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
                mDataSource.getDatabase().insert(ConfigsTable.TABLE_NAME, null, getConfigValues(true));
            } else {
                cursor.moveToFirst();
                mCoutDays = cursor.getCountDays();
                mFullDesc = cursor.getFullDesc();
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

    public boolean isFullDesc() {
        return mFullDesc;
    }

    public void setFullDesc(boolean fullDesc) {
        mFullDesc = fullDesc;
        updateConfig();
    }

    private void updateConfig() {
        mDataSource.getDatabase().update(ConfigsTable.TABLE_NAME, getConfigValues(false), ConfigsTable.Cols.ID + " = ?", new String[]{ DEF_ID });
    }

    private ContentValues getConfigValues(boolean withID) {
        ContentValues values = new ContentValues();
        if (withID) {
            values.put(ConfigsTable.Cols.ID, DEF_ID);
        }
        values.put(ConfigsTable.Cols.COUNT_DAYS, getCoutDays());
        values.put(ConfigsTable.Cols.FULL_DESC, isFullDesc() ? 1 : 0);
        return values;
    }
}
