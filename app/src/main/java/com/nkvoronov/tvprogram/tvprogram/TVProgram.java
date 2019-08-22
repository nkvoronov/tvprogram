package com.nkvoronov.tvprogram.tvprogram;

import java.util.Date;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVProgram {
    private int mId;
    private int mIndex;
    private Date mStop;
    private Date mStart;
    private int mCategory;
    private String mTitle;
    private int mTimeType;
    private String mNameChannel;
    private boolean mIsFavorites;
    private boolean mIsFavoritChannel;
    private TVProgramDataSource mDataSource;
    private TVProgramDescription mDescription;

    public TVProgram(int id, int index, Date start, Date stop, String title) {
        mId = id;
        mIndex = index;
        mNameChannel = null;
        mIsFavoritChannel = false;
        mStart = start;
        mStop = stop;
        mTitle = title;
        mCategory = 0;
        mDescription = null;
        mIsFavorites = false;
        mTimeType = 2;
        mDataSource = null;
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

    public String getNameChannel() {
        return mNameChannel;
    }

    public void setNameChannel(String nameChannel) {
        mNameChannel = nameChannel;
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

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int category) {
        mCategory = category;
    }

    public boolean isFavorites() {
        return mIsFavorites;
    }

    public void setFavorites(boolean favorites) {
        mIsFavorites = favorites;
    }

    public boolean isFavoritChannel() {
        return mIsFavoritChannel;
    }

    public void setFavoritChannel(boolean favoritChannel) {
        mIsFavoritChannel = favoritChannel;
    }

    public int getTimeType() {
        return mTimeType;
    }

    public void setTimeType(int timeType) {
        mTimeType = timeType;
    }

    public void setDataSource(TVProgramDataSource dataSource) {
        mDataSource = dataSource;
    }

    public TVProgramDescription getDescription() {
        return mDescription;
    }

    public void setDescription(TVProgramDescription fullDescription) {
        mDescription = fullDescription;
    }

    public void setOtherData() {
        //
    }

    public void changeFavorites() {
        mDataSource.programChangeFavorites(this);
        mIsFavorites = !mIsFavorites;
    }
}
