package com.nkvoronov.tvprogram.parser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.tvprogram.TVProgramsList;
import java.util.Date;

public class Parser {
    private TVChannelsList mChannels;
    private TVProgramsList mPrograms;
    private int mCountDay;
    private Boolean mFullDesc;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public Parser(Context context, SQLiteDatabase database, int countDay, Boolean fullDesc) {
        mCountDay = countDay;
        mFullDesc = fullDesc;
        mDatabase = database;
        mContext = context;
        mChannels = new TVChannelsList(context, database);
        mPrograms = new TVProgramsList(context, database);
    }

    public int getCountDay() {
        return mCountDay;
    }

    public void setCountDay(int countDay) {
        mCountDay = countDay;
    }

    public Boolean getFullDesc() {
        return mFullDesc;
    }

    public void setFullDesc(Boolean fullDesc) {
        mFullDesc = fullDesc;
    }

    public TVChannelsList getChannels() {
        return mChannels;
    }

    public TVProgramsList getPrograms() {
        return mPrograms;
    }

    public Context getContext() {
        return mContext;
    }

    public void getContent() {
        //
    }

    public void getContentDay(TVChannel channel, Date date) {
        //
    }

    public void runParser() {
        //
    }
}
