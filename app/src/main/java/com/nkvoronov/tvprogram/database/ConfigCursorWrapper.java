package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.database.MainDbSchema.ConfigsTable;

public class ConfigCursorWrapper extends CursorWrapper {
    public ConfigCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public int getCountDays() {
        return getInt(getColumnIndex(ConfigsTable.Cols.COUNT_DAYS));
    }

    public boolean getFullDesc() {
        return getInt(getColumnIndex(ConfigsTable.Cols.FULL_DESC)) == 1;
    }
}
