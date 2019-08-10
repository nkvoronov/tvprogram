package com.nkvoronov.tvprogram.tvprogram;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.ArrayList;
import java.util.List;

public class TVProgramList {
    public static final String STR_SCHEDULECHANNEL = "schedule_channel_%d_day_%s.html";
    public static final String STR_ELMDOCSELECT = "div[class~=(?:pasttime|onair|time)]";
    public static final String STR_ELMDOCTITLE = "div[class~=(?:pastprname2|prname2)]";
    public static final String STR_ELMDOCDESC = "div[class~=(?:pastdesc|prdesc)]";

    private List<TVProgram> mData;
    private SQLiteDatabase mDatabase;

    public TVProgramList(SQLiteDatabase database) {
        mDatabase = database;
        mData = new ArrayList<>();
    }

    public List<TVProgram> getData() {
        return mData;
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
}
