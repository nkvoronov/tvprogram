package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;

public class ChannelsCursorWrapper extends CursorWrapper {

    public ChannelsCursorWrapper(Cursor cursor) {
        super(cursor);
    }
}
