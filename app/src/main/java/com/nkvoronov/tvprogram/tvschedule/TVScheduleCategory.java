package com.nkvoronov.tvprogram.tvschedule;

public class TVScheduleCategory {
    private int mId;
    private String mName;
    private String mDictionary;

    public TVScheduleCategory(int id, String name, String dictionary) {
        mId = id;
        mName = name;
        mDictionary = dictionary;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDictionary() {
        return mDictionary;
    }

    public void setDictionary(String dictionary) {
        mDictionary = dictionary;
    }
}
