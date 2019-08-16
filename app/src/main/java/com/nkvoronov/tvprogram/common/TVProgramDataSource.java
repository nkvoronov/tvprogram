package com.nkvoronov.tvprogram.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.database.TVProgramBaseHelper;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.tvprogram.TVProgramsList;
import java.util.Date;

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

    public boolean isIndexSort() {
        return mAppConfig.isIndexSort();
    }

    public void setIndexSort(boolean indexSort) {
        mAppConfig.setIndexSort(indexSort);
    }

    public TVChannelsList getChannels(boolean isFavorites, int filter) {
        TVChannelsList channels = new TVChannelsList(mContext, mDatabase, isIndexSort());
        channels.loadFromDB(isFavorites, filter);
        return channels;
    };

    public TVProgramsList getPrograms(int type, int channel, Date date) {
        TVProgramsList programs = new TVProgramsList(mContext, mDatabase);
        programs.loadFromDB(type, channel, date);
        return programs;
    };

    public boolean checkUpdateProgram(int channel) {
        return false;
    }

}
