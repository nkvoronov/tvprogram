package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsAllTable;

public class ChannelsAllCursorWrapper extends CursorWrapper {
    public ChannelsAllCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public String getName() {
        return getString(getColumnIndex(ChannelsAllTable.Cols.NAME));
    }
}
