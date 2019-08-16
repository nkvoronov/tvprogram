package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.tvprogram.TVProgram;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.SchedulesTable;

import java.util.Date;

public class TVProgramsCursorWrapper extends CursorWrapper {

    public TVProgramsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVProgram getProgram() {
        int id = getInt(getColumnIndex(SchedulesTable.Cols.ID));
        int index = getInt(getColumnIndex(SchedulesTable.Cols.CHANNEL));
        int category = getInt(getColumnIndex(SchedulesTable.Cols.CATEGORY));
        long start = getLong(getColumnIndex(SchedulesTable.Cols.START));
        long stop = getLong(getColumnIndex(SchedulesTable.Cols.END));
        String title = getString(getColumnIndex(SchedulesTable.Cols.TITLE));

        TVProgram program = new TVProgram(id, index, new Date(start), new Date(stop), title);
        return program;
    }
}
