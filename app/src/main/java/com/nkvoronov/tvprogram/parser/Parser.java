package com.nkvoronov.tvprogram.parser;

import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.tvprogram.TVProgramList;
import java.util.Date;

public class Parser  implements Runnable{
    private TVChannelsList mChannels;
    private TVProgramList mPrograms;
    private int mCountDay;
    private Boolean mFullDesc;
    private String mOutXML;
    private SQLiteDatabase mDatabase;

    public Parser(SQLiteDatabase database, String outXML, int countDay, Boolean fullDesc) {
        mOutXML = outXML;
        mCountDay = countDay;
        mFullDesc = fullDesc;
        mDatabase = database;
        mChannels = new TVChannelsList(database, true);
        mPrograms = new TVProgramList(database);
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

    public String getOutXML() {
        return mOutXML;
    }

    public void setOutXML(String outXML) {
        mOutXML = outXML;
    }

    public TVChannelsList getChannels() {
        return mChannels;
    }

    public TVProgramList getPrograms() {
        return mPrograms;
    }

    public void saveXML() {

    }

    public void getContent() {

    }

    public void getContentDay(TVChannel channel, Date date) {

    }

    public void runParser() {

    }

    @Override
    public void run() {
        runParser();
    }
}
