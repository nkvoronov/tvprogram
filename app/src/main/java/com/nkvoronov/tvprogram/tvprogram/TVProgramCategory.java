package com.nkvoronov.tvprogram.tvprogram;

public class TVProgramCategory {
    private int mId;
    private String mName;
    private String mColor;
    private String mDictionary;

    public TVProgramCategory(int id, String name, String dictionary, String color) {
        mId = id;
        mName = name;
        mDictionary = dictionary;
        mColor = color;
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

    public String getColor() {
        return mColor;
    }

    public void setColor(String color) {
        mColor = color;
    }
}
