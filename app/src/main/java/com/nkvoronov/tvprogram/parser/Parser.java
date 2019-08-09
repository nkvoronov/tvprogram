package com.nkvoronov.tvprogram.parser;

import android.content.Context;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvprogram.TVProgramList;
import java.util.Date;

public class Parser  implements Runnable{
    //private ChannelList mChannels;
    private TVProgramList mPrograms;
    private int mCountDay;
    private Boolean mFullDesc;
    private String mLang;
    private String mOutXML;
    private Context mContext;

    public Parser(Context context, String outXML, String lang, int countDay, Boolean fullDesc) {
        mContext = context.getApplicationContext();
        mOutXML = outXML;
        mCountDay = countDay;
        mFullDesc = fullDesc;
        mLang = lang;
        //mChannels = new ChannelList(mContext, mLang, true);
        mPrograms = new TVProgramList(mContext);
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

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        mLang = lang;
    }

//    public ChannelList getChannels() {
//        return mChannels;
//    }

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
