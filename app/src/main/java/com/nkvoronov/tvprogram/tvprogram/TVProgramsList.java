package com.nkvoronov.tvprogram.tvprogram;

import java.util.Date;
import java.util.List;
import android.util.Log;
import java.util.ArrayList;
import android.database.Cursor;
import java.text.SimpleDateFormat;
import android.content.ContentValues;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import com.nkvoronov.tvprogram.database.TVProgramsCursorWrapper;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLProgramsForChannelToDate;

public class TVProgramsList {
    private List<TVProgram> mData;
    private TVProgramDataSource mDataSource;

    public TVProgramsList(TVProgramDataSource dataSource) {
        mData = new ArrayList<>();
        mDataSource = dataSource;
    }

    public List<TVProgram> getData() {
        return mData;
    }

    public TVProgram get(int position) {
        return getData().get(position);
    }

    public TVProgram getForId(int id) {
        TVProgram program = null;
        for (int i = 0; i < size(); i++) {
            if (get(i).getId() == id) {
                program = get(i);
                break;
            }
        }
        return program;
    }

    public int size() {
        return getData().size();
    }

    public void clear() {
        getData().clear();
    }

    public void add(TVProgram program) {
        program.setDataSource(mDataSource);
        getData().add(program);
    }

    public void loadFromDB(int type, int channel, Date date) {
        TVProgramsCursorWrapper cursor = null;
        clear();

        switch (type) {
            case 0:
                cursor = queryPrograms(getSQLProgramsForChannelToDate(channel, date),null);
                break;
            case 1:
                break;
            default:
                break;
        }

        try {
            Log.d(TAG, Integer.toString(cursor.getCount()));
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TVProgram program = cursor.getProgram();
                add(program);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    private TVProgramsCursorWrapper queryPrograms(String sql, String[] selectionArgs) {
        Cursor cursor = mDataSource.getDatabase().rawQuery(sql, selectionArgs);
        return new TVProgramsCursorWrapper(cursor);
    }

    public void setProgramStop() {
        int i = 0;
        while (i != size()) {
            TVProgram program = get(i);
            TVProgram program_next;
            if (i + 1 != size()) {
                program_next = get(i+1);
                if (program_next.getIndex() == program_next.getIndex()) {
                    program.setStop(program_next.getStart());
                }
            }
            i++;
        }
    }

    public void saveToDB() {
        for (TVProgram program : getData()) {
            mDataSource.getDatabase().insert(SchedulesTable.TABLE_NAME, null, getContentProgramsValues(program));
        }
    }

    private ContentValues getContentProgramsValues(TVProgram program) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ContentValues values = new ContentValues();
        values.put(SchedulesTable.Cols.CHANNEL, program.getIndex());
        values.put(SchedulesTable.Cols.CATEGORY, program.getCategory());
        values.put(SchedulesTable.Cols.START, simpleDateFormat.format(program.getStart()));
        values.put(SchedulesTable.Cols.END, simpleDateFormat.format(program.getStop()));
        values.put(SchedulesTable.Cols.TITLE, program.getTitle());
        return values;
    }

    public void preUpdateProgram(int channel) {
        mDataSource.getDatabase().delete(SchedulesTable.TABLE_NAME, SchedulesTable.Cols.CHANNEL + "=?", new String[]{ String.valueOf(channel) });
    }

    private int getCategoryID(String nameEN, String nameRU) {
        return -1;
    }

    private int saveToDBCategory(TVProgram program) {
        return -1;
    }

    private int getDescriptionID(String description) {
        return -1;
    }

    private void saveToDBDescription(int scheduleID, TVProgram program) {
        //
    }

    private int getGenreID(String name) {
        return -1;
    }

    private void saveToDBCategoryList(String[] list, int descriptionID) {

    }

    private void saveToDBGenre(int descriptionID, TVProgram program) {
        //
    }

    private int getCreditID(String name, int type) {
        return -1;
    }

    private void saveToDBCreditList(String[] list, int type, int descriptionID) {
        //
    }

    private void saveToDBCredits(int descriptionID, TVProgram program) {
        //
    }

    private int getInsertScheduleID(TVProgram program) {
        return -1;
    }

    private int getUserChannelID(int index) {
        return -1;
    }

    public int clearDBSchedule() {
        return -1;
    }

    public TVProgram getProgrammeForUrl(String url) {
        return null;
    }
}
