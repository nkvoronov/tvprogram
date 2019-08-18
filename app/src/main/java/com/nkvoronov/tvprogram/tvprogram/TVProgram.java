package com.nkvoronov.tvprogram.tvprogram;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TVProgram {
    public static final String SEPARATOR_LIST = ",";

    private int mId;
    private int mIndex;
    private Date mStart;
    private Date mStop;
    private String mCorrectionTime;
    private String mTitle;
    private String mDescription;
    private String mUrlFullDesc;
    private String mCategory;
    private String mGenres;
    private String mDirectors;
    private String mActors;
    private String mDate;
    private String mCountry;
    private String mImage;
    private String mStarRating;

    public static String addZero(int value) {
        if (value < 10) {
            return "0" + Integer.toString(value);
        } else {
            return Integer.toString(value);
        }
    }

    public static String intToTime(int value, String sep, Boolean isZN, Boolean isSec) {
        String zn = "";
        int del = 60;
        if (isSec) {
            del = del * 60;
        }
        int crh = value / del;
        int crm;
        if (isSec) {
            crm = (value - crh * del) / 60;
        } else crm = value - crh * del;
        if (isZN) {
            if (value >= 0) {
                zn = "+";
            } else zn = "-";
        }
        return zn + addZero(crh) + sep + addZero(crm);
    }

    public TVProgram(int id, int index, Date start, Date stop, String title) {
        mId = id;
        mIndex = index;
        mStart = start;
        mStop = stop;
        mCorrectionTime = null;
        mTitle = title;
        mDescription = null;
        mUrlFullDesc = null;
        mCategory = null;
        mGenres = null;
        mDirectors = null;
        mActors = null;
        mDate = null;
        mCountry = null;
        mImage = null;
        mStarRating = null;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public Date getStart() {
        return mStart;
    }

    public void setStart(Date start) {
        mStart = start;
    }

    public Date getStop() {
        return mStop;
    }

    public void setStop(Date stop) {
        mStop = stop;
    }

    public void setCorrectionTime(int correction) {
        mCorrectionTime = intToTime(correction, "", true, false);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String description) {
        mDescription = description;
    }

    public String getUrlFullDesc() {
        return mUrlFullDesc;
    }

    public void setUrlFullDesc(String urlFullDesc) {
        mUrlFullDesc = urlFullDesc;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(String genres) {
        mGenres = genres;
    }

    public String getDirectors() {
        return mDirectors;
    }

    public void setDirectors(String directors) {
        mDirectors = directors;
    }

    public String getActors() {
        return mActors;
    }

    public void setActors(String actors) {
        mActors = actors;
    }

    public String getDate() {
        return mDate;
    }

    public void setSDate(String sDate) {
        mDate = sDate;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getStarRating() {
        return mStarRating;
    }

    public void setStarRating(String starRating) {
        mStarRating = starRating;
    }

    private String getDateToFormat(Date date) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmss");
        String result = date_format.format(date);
        if (mCorrectionTime != null) {
            result = result + " " + mCorrectionTime;
        }
        return result;
    }
}
