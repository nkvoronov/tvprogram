package com.nkvoronov.tvprogram.database;

import java.util.Date;
import android.util.Log;
import android.database.Cursor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.tvprogram.TVProgram;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.SchedulesTable;

public class TVProgramsCursorWrapper extends CursorWrapper {

    public TVProgramsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVProgram getProgram() {
        int id = getInt(getColumnIndex(SchedulesTable.Cols.ID));
        int index = getInt(getColumnIndex(SchedulesTable.Cols.CHANNEL));
        String name = getString(getColumnIndex(SchedulesTable.Cols.NAME));
        int favorite_channel = getInt(getColumnIndex(SchedulesTable.Cols.FAVORITE_CHANNEL));
        int category = getInt(getColumnIndex(SchedulesTable.Cols.CATEGORY));
        String start = getString(getColumnIndex(SchedulesTable.Cols.STARTING));
        String stop = getString(getColumnIndex(SchedulesTable.Cols.ENDING));
        String title = getString(getColumnIndex(SchedulesTable.Cols.TITLE));
        int timeType = getInt(getColumnIndex(SchedulesTable.Cols.TIME_TYPE));
        int favorite = getInt(getColumnIndex(SchedulesTable.Cols.FAVORITE));

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
        program.setNameChannel(name);
        program.setCategory(category);
        program.setTimeType(timeType);
        program.setFavoritChannel(favorite_channel == 1);
        program.setFavorites(favorite == 1);
        program.setDescriptionFromDB();
        return program;
    }
}
