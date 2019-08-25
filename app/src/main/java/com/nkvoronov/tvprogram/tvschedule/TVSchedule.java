package com.nkvoronov.tvprogram.tvschedule;

import java.util.Date;
import android.database.Cursor;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import static com.nkvoronov.tvprogram.common.DateUtils.getDateFormat;

public class TVSchedule {
    private int mId;
    private int mIndex;
    private Date mEnding;
    private Date mStarting;
    private int mCategory;
    private String mTitle;
    private int mTimeType;
    private String mNameChannel;
    private boolean mIsFavorites;
    private boolean mIsFavoritesChannel;
    private boolean mExDesc;
    private MainDataSource mDataSource;
    private TVScheduleDescription mDescription;

    public TVSchedule(int id, int index, Date starting, Date ending, String title) {
        mId = id;
        mIndex = index;
        mNameChannel = null;
        mIsFavoritesChannel = false;
        mStarting = starting;
        mEnding = ending;
        mTitle = title;
        mCategory = 0;
        mDescription = null;
        mIsFavorites = false;
        mExDesc = false;
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

    public boolean isFavoritesChannel() {
        return mIsFavoritesChannel;
    }

    public void setFavoritesChannel(boolean favoritChannel) {
        mIsFavoritesChannel = favoritChannel;
    }

    public boolean isExDesc() {
        return mExDesc;
    }

    public void setExDesc(boolean exDesc) {
        mExDesc = exDesc;
    }

    public int getTimeType() {
        return mTimeType;
    }

    public void setTimeType(int timeType) {
        mTimeType = timeType;
    }

    public void setDataSource(MainDataSource dataSource) {
        mDataSource = dataSource;
    }

    public TVScheduleDescription getDescription() {
        return mDescription;
    }

    public void setDescription(TVScheduleDescription description) {
        mDescription = description;
    }

    public void setIdFromDB() {
        mId = -1;
        String title = getTitle();
        String starting = getDateFormat(getStarting(), "yyyy-MM-dd HH:mm:ss");
        String ending = getDateFormat(getEnding(), "yyyy-MM-dd HH:mm:ss");

        Cursor cursor = mDataSource.getDatabase().query(SchedulesTable.TABLE_NAME,
                null,
                "(" + SchedulesTable.Cols.TITLE + "=?) AND (" + SchedulesTable.Cols.STARTING + "=?) AND (" + SchedulesTable.Cols.ENDING + "=?)",
                new String[]{title, starting, ending},
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
        if (isExDesc()) {
            TVScheduleDescription scheduleDescription = new TVScheduleDescription("");
            scheduleDescription.setDataSource(mDataSource);
            scheduleDescription.loadFromDB(getId());
            setDescription(scheduleDescription);
        }
    }

    public void changeFavorites() {
        mDataSource.scheduleChangeFavorites(this);
        mIsFavorites = !mIsFavorites;
    }
}
