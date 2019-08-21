package com.nkvoronov.tvprogram.common;

import java.util.Date;
import android.util.Log;
import android.content.Context;
import android.database.Cursor;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.tvprogram.TVProgram;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvprogram.TVProgramsList;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import com.nkvoronov.tvprogram.database.TVProgramBaseHelper;
import static com.nkvoronov.tvprogram.common.StringUtils.addQuotes;

public class TVProgramDataSource {
    public static final String RUS_LANG = "rus";
    public static final String UKR_LANG = "ukr";
    public static final String ALL_LANG = "all";
    public static final String TAG = "TVPROGRAM";

    private Context mContext;
    private AppConfig mAppConfig;
    private SQLiteDatabase mDatabase;
    private static TVProgramDataSource sTVProgramLab;

    public TVProgramDataSource(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TVProgramBaseHelper(mContext).getWritableDatabase();
        mAppConfig = new AppConfig(mDatabase);
    }

    public static TVProgramDataSource get(Context context) {
        if (sTVProgramLab == null) {
            sTVProgramLab = new TVProgramDataSource(context);
        }
        return sTVProgramLab;
    }

    public int getCoutDays() {
        return mAppConfig.getCoutDays();
    }

    public void setCoutDays(int coutDays) {
        mAppConfig.setCoutDays(coutDays);
    }

    public Context getContext() {
        return mContext;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public void channelChangeFavorites(TVChannel channel) {
        if (channel.isFavorites()) {
            mDatabase.delete(ChannelsFavoritesTable.TABLE_NAME, ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " = ?", new String[]{String.valueOf(channel.getIndex())});
        } else {
            mDatabase.insert(ChannelsFavoritesTable.TABLE_NAME, null, getContentChannelsFavValues(channel));
        }
    }

    private ContentValues getContentChannelsFavValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        values.put(ChannelsFavoritesTable.Cols.CHANNEL_INDEX, channel.getIndex());
        return values;
    }

    public TVChannelsList getChannels(boolean isFavorites, int filter) {
        TVChannelsList channels = new TVChannelsList(this);
        channels.loadFromDB(isFavorites, filter);
        return channels;
    };

    public TVChannel getChannel(int index) {
        TVChannelsList channelList = getChannels(false, 0);
        return channelList.getForIndex(index);
    }

    public TVProgramsList getPrograms(int type, int channel, Date date) {
        TVProgramsList programs = new TVProgramsList(this);
        programs.loadFromDB(type, channel, date);
        return programs;
    };

    public TVProgram getProgram(int index, int programID) {
        TVProgramsList programs = getPrograms(0, index, null);
        return programs.getForId(programID);
    }

    public boolean checkUpdateProgram(int channel) {
        String index = new Integer(channel).toString();
        String now_date = addQuotes(getDateFormat(new Date(), "yyyy-MM-dd"), "'");
        boolean res = true;
        Cursor cursor = mDatabase.query(SchedulesTable.TABLE_NAME,
                null,
                "(" + SchedulesTable.Cols.CHANNEL + "=" + index + ") and (" + SchedulesTable.Cols.START + ">=" + now_date + ")",
                null,
                null,
                null,
                null);
        try {
           if (cursor.getCount() != 0) {
               Log.d(TAG, "No Update Program !!! " + now_date + ":" + index);
               res = false;
           } else {
               Log.d(TAG, "Update Program !!! " + now_date + ":" + index);
           }
        } finally {
            cursor.close();
        }

        return res;
    }

    public long[] getProgramInfo(int channel) {
        long[] info = {0, 0, 0};
        String sChannel = String.valueOf(channel);
        String sql =
                "SELECT " +
                "min(" + SchedulesTable.Cols.START + ") as min, " +
                "max(" + SchedulesTable.Cols.START + ") as max " +
                "FROM " +
                SchedulesTable.TABLE_NAME + " " +
                "WHERE " + SchedulesTable.Cols.CHANNEL + "=" + sChannel;

        Cursor cursor = mDatabase.rawQuery(sql, null, null);
        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                String min = cursor.getString(cursor.getColumnIndex("min"));
                String max = cursor.getString(cursor.getColumnIndex("max"));
                String now = getDateFormat(new Date(), "yyyy-MM-dd");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date minDate = null;
                Date maxDate = null;
                Date nowDate = null;

                try {
                    minDate = simpleDateFormat.parse(min);
                    maxDate = simpleDateFormat.parse(max);
                    nowDate = simpleDateFormat.parse(now);
                } catch (ParseException e) {
                    e.fillInStackTrace();
                    Log.d(TAG, e.getMessage());
                }

                info[0] = minDate.getTime();
                info[1] = (maxDate.getTime() - minDate.getTime()) / 86400000;
                info[2] = (nowDate.getTime() - minDate.getTime()) / 86400000;
            }
        } finally {
            cursor.close();
        }
        return info;
    }
}
