package com.nkvoronov.tvprogram.tvschedule;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.content.ContentValues;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import static com.nkvoronov.tvprogram.database.MainBaseHelper.*;
import com.nkvoronov.tvprogram.database.TVSchedulesCursorWrapper;
import static com.nkvoronov.tvprogram.common.DateUtils.getDateFormat;

public class TVSchedulesList {
    private List<TVSchedule> mData;
    private MainDataSource mDataSource;

    public TVSchedulesList(MainDataSource dataSource) {
        mData = new ArrayList<>();
        mDataSource = dataSource;
    }

    public List<TVSchedule> getData() {
        return mData;
    }

    public TVSchedule get(int position) {
        return getData().get(position);
    }

    public TVSchedule getForId(int id) {
        TVSchedule schedule = null;
        TVSchedulesCursorWrapper cursor = querySchedules(getSQLProgramsForChannelToDay(id, "", null),null);

        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                schedule = cursor.getSchedule();
                schedule.setDataSource(mDataSource);
            }
        } finally {
            cursor.close();
        }

        return schedule;
    }

    public int size() {
        return getData().size();
    }

    public void clear() {
        getData().clear();
    }

    public void add(TVSchedule schedule) {
        schedule.setDataSource(mDataSource);
        getData().add(schedule);
    }

    public void loadFromDB(int type, String filter, Date date) {
        TVSchedulesCursorWrapper cursor = null;
        clear();

        switch (type) {
            case 0:
                cursor = querySchedules(getSQLProgramsForChannelToDay(-1, filter, date),null);
                break;
            case 1:
                cursor = querySchedules(getSQLNowPrograms(filter),null);
                break;
            case 2:
                cursor = querySchedules(getSQLFavoritesPrograms(),null);
                break;
            case 3:
                cursor = querySchedules(getSQLSearchPrograms(filter),null);
                break;
            default:
                break;
        }

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TVSchedule schedule = cursor.getSchedule();
                add(schedule);
                schedule.setDescriptionFromDB();
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    private TVSchedulesCursorWrapper querySchedules(String sql, String[] selectionArgs) {
        Cursor cursor = mDataSource.getDatabase().rawQuery(sql, selectionArgs);
        return new TVSchedulesCursorWrapper(cursor);
    }

    public void setScheduleEnding() {
        int i = 0;
        while (i != size()) {
            TVSchedule schedule = get(i);
            TVSchedule schedule_next;
            if (i + 1 != size()) {
                schedule_next = get(i+1);
                if (schedule_next.getIndex() == schedule_next.getIndex()) {
                    schedule.setEnding(schedule_next.getStarting());
                }
            }
            i++;
        }
    }

    public TVSchedule getScheduleForType(String type, int catalog) {
        for (TVSchedule schedule : getData()) {
            if (schedule.getDescription() != null &&
                    schedule.getDescription().getType() != null &&
                    schedule.getDescription().getType().equals(type) &&
                    schedule.getDescription().getIdCatalog() == catalog) {
                return schedule;
            }
        }
        return null;
    }

    public void saveToDB() {
        for (TVSchedule schedule : getData()) {
            mDataSource.getDatabase().insert(SchedulesTable.TABLE_NAME, null, getContentSchedulesValues(schedule));
            if (schedule.getDescription() != null) {
                schedule.getDescription().setDataSource(mDataSource);
                schedule.setIdFromDB();
                schedule.getDescription().setIdSchedule(schedule.getId());
                schedule.getDescription().setIdFromDB();
                if (schedule.getDescription().getIdDescription() == -1) {
                    schedule.getDescription().saveToDB();
                    schedule.getDescription().setIdFromDB();
                } else {
                    schedule.getDescription().updateDB();
                }
                mDataSource.getDatabase().insert(ScheduleDescriptionTable.TABLE_NAME, null, getContentScheduleDescriptionValues(schedule.getDescription()));
            }
        }
    }

    private ContentValues getContentSchedulesValues(TVSchedule schedule) {
        ContentValues values = new ContentValues();
        values.put(SchedulesTable.Cols.CHANNEL, schedule.getIndex());
        values.put(SchedulesTable.Cols.CATEGORY, schedule.getCategory());
        values.put(SchedulesTable.Cols.STARTING, getDateFormat(schedule.getStarting(), "yyyy-MM-dd HH:mm:ss"));
        values.put(SchedulesTable.Cols.ENDING, getDateFormat(schedule.getEnding(), "yyyy-MM-dd HH:mm:ss"));
        values.put(SchedulesTable.Cols.TITLE, schedule.getTitle());
        return values;
    }

    private ContentValues getContentScheduleDescriptionValues(TVScheduleDescription scheduleDescription) {
        ContentValues values = new ContentValues();
        values.put(ScheduleDescriptionTable.Cols.SCHEDULE, scheduleDescription.getIdSchedule());
        values.put(ScheduleDescriptionTable.Cols.DESCRIPTION, scheduleDescription.getIdDescription());
        return values;
    }

    public void preUpdateSchedules(int channel) {
        mDataSource.getDatabase().delete(SchedulesTable.TABLE_NAME, SchedulesTable.Cols.CHANNEL + "=?", new String[]{ String.valueOf(channel) });
    }
}
