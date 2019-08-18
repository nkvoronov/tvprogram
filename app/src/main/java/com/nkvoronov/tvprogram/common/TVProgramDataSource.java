package com.nkvoronov.tvprogram.common;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.nkvoronov.tvprogram.database.TVProgramBaseHelper;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.tvprogram.TVProgramsList;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;

public class TVProgramDataSource {
    public static final String RUS_LANG = "rus";
    public static final String UKR_LANG = "ukr";
    public static final String ALL_LANG = "all";
    public static final String TAG = "TVPROGRAM";

    private static TVProgramDataSource sTVProgramLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;
    private AppConfig mAppConfig;

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

    public TVChannelsList getChannels(boolean isFavorites, int filter) {
        TVChannelsList channels = new TVChannelsList(mContext, mDatabase);
        channels.loadFromDB(isFavorites, filter);
        return channels;
    };

    public TVProgramsList getPrograms(int type, int channel, Date date) {
        TVProgramsList programs = new TVProgramsList(mContext, mDatabase);
        programs.loadFromDB(type, channel, date);
        return programs;
    };

    public boolean checkUpdateProgram(int channel) {
        String index = String.valueOf(channel);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        String now_date = "'" + simpleDateFormat.format(calendar.getTime()) + "'";
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

}
