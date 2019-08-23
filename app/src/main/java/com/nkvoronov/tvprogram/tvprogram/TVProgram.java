package com.nkvoronov.tvprogram.tvprogram;

import java.util.Date;
import android.database.Cursor;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import static com.nkvoronov.tvprogram.common.StringUtils.addQuotes;
import static com.nkvoronov.tvprogram.common.DateUtils.getDateFormat;

public class TVProgram {
    private int mId;
    private int mIndex;
    private Date mEnding;
    private Date mStarting;
    private int mCategory;
    private String mTitle;
    private int mTimeType;
    private String mNameChannel;
    private boolean mIsFavorites;
    private boolean mIsFavoritChannel;
    private TVProgramDataSource mDataSource;
    private TVProgramDescription mDescription;

    public TVProgram(int id, int index, Date starting, Date ending, String title) {
        mId = id;
        mIndex = index;
        mNameChannel = null;
        mIsFavoritChannel = false;
        mStarting = starting;
        mEnding = ending;
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

    public Date getStarting() {
        return mStarting;
    }

    public void setStarting(Date starting) {
        mStarting = starting;
    }

    public Date getEnding() {
        return mEnding;
    }

    public void setEnding(Date ending) {
        mEnding = ending;
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

    public void setIdFromDB() {
        mId = -1;
        String title = addQuotes(getTitle(), "'");
        String starting = addQuotes(getDateFormat(getStarting(), "yyyy-MM-dd HH:mm:ss"), "'");
        String ending = addQuotes(getDateFormat(getEnding(), "yyyy-MM-dd HH:mm:ss"), "'");

        Cursor cursor = mDataSource.getDatabase().query(SchedulesTable.TABLE_NAME,
                null,
                "(" + SchedulesTable.Cols.TITLE + "=" + title + ") and (" + SchedulesTable.Cols.STARTING + "=" + starting + ") and (" + SchedulesTable.Cols.ENDING + "=" + ending + ")",
                null,
                null,
                null,
                null);

        try {

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                mId = cursor.getInt(cursor.getColumnIndex(SchedulesTable.Cols.ID));
            }
        } finally {
            cursor.close();
        }
    }

    public void setDescriptionFromDB() {
        //mDescription = new TVProgramDescription("");
    }

    public void changeFavorites() {
        mDataSource.programChangeFavorites(this);
        mIsFavorites = !mIsFavorites;
    }
}
