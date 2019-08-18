package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.Log;
import com.nkvoronov.tvprogram.tvprogram.TVProgram;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.SchedulesTable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;

public class TVProgramsCursorWrapper extends CursorWrapper {

    public TVProgramsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVProgram getProgram() {
        int id = getInt(getColumnIndex(SchedulesTable.Cols.ID));
        int index = getInt(getColumnIndex(SchedulesTable.Cols.CHANNEL));
        int category = getInt(getColumnIndex(SchedulesTable.Cols.CATEGORY));
        String start = getString(getColumnIndex(SchedulesTable.Cols.START));
        String stop = getString(getColumnIndex(SchedulesTable.Cols.END));
        String title = getString(getColumnIndex(SchedulesTable.Cols.TITLE));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        Date endDate = null;

        try {
            startDate = simpleDateFormat.parse(start);
            endDate = simpleDateFormat.parse(stop);
        } catch (ParseException e) {
            e.fillInStackTrace();
            Log.d(TAG, e.getMessage());
        }

        TVProgram program = new TVProgram(id, index, startDate, endDate, title);
        program.setCategory(category);
        return program;
    }
}
