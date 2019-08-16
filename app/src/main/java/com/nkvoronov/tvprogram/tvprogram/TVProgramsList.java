package com.nkvoronov.tvprogram.tvprogram;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import com.nkvoronov.tvprogram.database.TVProgramsCursorWrapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLProgramsForChannelToDate;

public class TVProgramsList {
    private List<TVProgram> mData;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public TVProgramsList(Context context, SQLiteDatabase database) {
        mData = new ArrayList<>();
        mContext = context;
        mDatabase = database;
    }

    public List<TVProgram> getData() {
        return mData;
    }

    public TVProgram get(int position) {
        return mData.get(position);
    }

    public int size() {
        return mData.size();
    }

    public void clear() {
        mData.clear();
    }

    public void add(TVProgram channel) {
        mData.add(channel);
    }

    public void loadFromDB(int type, int channel, Date date) {
        TVProgramsCursorWrapper cursor = null;
        mData.clear();

        switch (type) {
            case 0:
                cursor = queryPrograms(getSQLProgramsForChannelToDate(channel, date),null);
                Log.d(TAG, "type : " + type);
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
                mData.add(program);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    private TVProgramsCursorWrapper queryPrograms(String sql, String[] selectionArgs) {
        Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
        return new TVProgramsCursorWrapper(cursor);
    }

    public void saveToDB() {
        //
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

    public void setProgramStop() {
        //
    }

    public int clearDBSchedule() {
        return -1;
    }

    public TVProgram getProgrammeForUrl(String url) {
        return null;
    }

    @Override
    public String toString() {
        String result = "";
        for (TVProgram program : mData) {
            result += program.toString() + "/n";
        }
        return result;
    }

    public void getXML(Document document, Element element) {
        for (TVProgram program : mData) {
            program.getXML(document, element);
        }
    }

    private ContentValues getContentProgramsValues(TVProgram program) {
        ContentValues values = new ContentValues();
        values.put(SchedulesTable.Cols.CHANNEL, program.getIndex());
        values.put(SchedulesTable.Cols.CATEGORY, program.getCategory());
        values.put(SchedulesTable.Cols.START, program.getStart().getTime());
        values.put(SchedulesTable.Cols.END, program.getStop().getTime());
        values.put(SchedulesTable.Cols.TITLE, program.getTitle());
        return values;
    }
}
