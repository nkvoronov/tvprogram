package com.nkvoronov.tvprogram.database;

import java.util.Date;
import android.util.Log;
import android.database.Cursor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.tvschedule.TVSchedule;
import static com.nkvoronov.tvprogram.common.MainDataSource.TAG;
import com.nkvoronov.tvprogram.database.MainDbSchema.SchedulesTable;

public class TVSchedulesCursorWrapper extends CursorWrapper {

    public TVSchedulesCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVSchedule getSchedule() {
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
        int exdesc = getInt(getColumnIndex(SchedulesTable.Cols.EXDESC));

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

        TVSchedule schedule = new TVSchedule(id, index, startDate, endDate, title);
        schedule.setNameChannel(name);
        schedule.setCategory(category);
        schedule.setTimeType(timeType);
        schedule.setFavoritesChannel(favorite_channel == 1);
        schedule.setFavorites(favorite == 1);
        schedule.setExDesc(exdesc == 1);
        return schedule;
    }
}
