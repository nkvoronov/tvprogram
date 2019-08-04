package com.nkvoronov.tvprogram.parser;

import android.content.Context;
import com.nkvoronov.tvprogram.common.Channel;
import com.nkvoronov.tvprogram.common.ChannelList;
import com.nkvoronov.tvprogram.common.ProgramList;
import java.util.Date;

public class Parser  implements Runnable{
    private ChannelList mChannels;
    private ProgramList mPrograms;
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
        mChannels = new ChannelList(mContext, mLang, true);
        mPrograms = new ProgramList(mContext);
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

    public ChannelList getChannels() {
        return mChannels;
    }

    public ProgramList getPrograms() {
        return mPrograms;
    }

    public void saveXML() {

    }

    public void getContent() {

    }

    public void getContentDay(Channel channel, Date date) {

    }

    public void runParser() {

    }

    @Override
    public void run() {
        runParser();
    }
}
