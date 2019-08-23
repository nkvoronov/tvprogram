package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.database.MainDbSchema.ChannelsTable;

public class TVChannelsNameCursorWrapper extends CursorWrapper {
    public TVChannelsNameCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getName() {
        return getString(getColumnIndex(ChannelsTable.Cols.NAME));
    }
}
