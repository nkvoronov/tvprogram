package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;

public class TVProgramCursorWrapper extends CursorWrapper {
    public TVProgramCursorWrapper(Cursor cursor) {
        super(cursor);
    }
}
