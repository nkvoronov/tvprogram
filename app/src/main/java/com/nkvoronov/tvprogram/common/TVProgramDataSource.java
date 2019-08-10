package com.nkvoronov.tvprogram.common;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.database.TVProgramBaseHelper;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;

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

    public TVChannelsList getChannels(boolean isFavorites) {
        TVChannelsList channels = new TVChannelsList(mDatabase, isIndexSort());
        channels.loadFromDB(isFavorites);
        return channels;
    };

    public void updateChannels() {
        TVChannelsList channels = new TVChannelsList(mDatabase, isIndexSort());
        channels.loadFromNetAndUpdate(true);
    }
}
