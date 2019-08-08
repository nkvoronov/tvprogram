package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;

public class TvProgramsCursorWrapper extends CursorWrapper {
    public TvProgramsCursorWrapper(Cursor cursor) {
        super(cursor);
    }
}
