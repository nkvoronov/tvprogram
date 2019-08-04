package com.nkvoronov.tvprogram.common;

import android.content.Context;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

public class ProgramList {
    public static final String STR_SCHEDULECHANNEL = "schedule_channel_%d_day_%s.html";
    public static final String STR_ELMDOCSELECT = "div[class~=(?:pasttime|onair|time)]";
    public static final String STR_ELMDOCTITLE = "div[class~=(?:pastprname2|prname2)]";
    public static final String STR_ELMDOCDESC = "div[class~=(?:pastdesc|prdesc)]";

    private Context mContext;
    private List<Program> mData;

    public ProgramList(Context context) {
        mContext = context.getApplicationContext();
        mData = new ArrayList<>();
    }

    public List<Program> getData() {
        return mData;
    }

    public void saveToDB() {
        //
    }

    private int getCategoryID(String nameEN, String nameRU) {
        return -1;
    }

    private int saveToDBCategory(Program program) {
        return -1;
    }

    private int getDescriptionID(String description) {
        return -1;
    }

    private void saveToDBDescription(int scheduleID, Program program) {
        //
    }

    private int getGenreID(String name) {
        return -1;
    }

    private void saveToDBCategoryList(String[] list, int descriptionID) {

    }

    private void saveToDBGenre(int descriptionID, Program program) {
        //
    }

    private int getCreditID(String name, int type) {
        return -1;
    }

    private void saveToDBCreditList(String[] list, int type, int descriptionID) {
        //
    }

    private void saveToDBCredits(int descriptionID, Program program) {
        //
    }

    private int getInsertScheduleID(Program program) {
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

    public Program getProgrammeForUrl(String url) {
        return null;
    }

    @Override
    public String toString() {
        String result = "";
        for (Program program : mData) {
            result += program.toString() + "/n";
        }
        return result;
    }

    public void getXML(Document document, Element element) {
        for (Program program : mData) {
            program.getXML(document, element);
        }
    }
}
