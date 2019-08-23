package com.nkvoronov.tvprogram.tvschedule;

import android.util.Log;
import android.database.Cursor;
import android.content.ContentValues;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import static com.nkvoronov.tvprogram.common.MainDataSource.TAG;
import static com.nkvoronov.tvprogram.common.StringUtils.addQuotes;
import com.nkvoronov.tvprogram.database.TVScheduleDescriptionCursorWrapper;
import static com.nkvoronov.tvprogram.database.MainBaseHelper.getSQLDescription;

public class TVScheduleDescription {
    private String mYear;
    private String mImage;
    private int mIdSchedule;
    private String mRating;
    private String mActors;
    private String mGenres;
    private String mCountry;
    private String mDirectors;
    private int mIdDescription;
    private String mDescription;
    private String mUrlFullDesc;
    private MainDataSource mDataSource;

    public TVScheduleDescription(String description) {
        this(-1, -1, description);
    }

    public TVScheduleDescription(int id, int schedule, String description) {
        mIdDescription = id;
        mIdSchedule = schedule;
        mDescription = description;
        mYear = null;
        mImage = null;
        mActors = null;
        mGenres = null;
        mCountry = null;
        mDirectors = null;
        mRating = null;
        mUrlFullDesc = null;
        mDataSource = null;
    }

    public int getIdDescription() {
        return mIdDescription;
    }

    public void setIdDescription(int id) {
        mIdDescription = id;
    }

    public int getIdSchedule() {
        return mIdSchedule;
    }

    public void setIdSchedule(int schedule) {
        mIdSchedule = schedule;
    }

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String image) {
        mImage = image;
    }

    public String getActors() {
        return mActors;
    }

    public void setActors(String actors) {
        mActors = actors;
    }

    public String getGenres() {
        return mGenres;
    }

    public void setGenres(String genres) {
        mGenres = genres;
    }

    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry =country;
    }

    public String getDirectors() {
        return mDirectors;
    }

    public void setDirectors(String directors) {
        mDirectors = directors;
    }

    public String getRating() {
        return mRating;
    }

    public void setRating(String rating) {
        mRating = rating;
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

    public void setDataSource(MainDataSource dataSource) {
        mDataSource = dataSource;
    }

    public void setIdFromDB() {
        mIdDescription = -1;
        String desc = addQuotes(getDescription().replaceAll("'", "''"), "'");
        Log.d(TAG, "setIdFromDB - " + desc);
        Cursor cursor = mDataSource.getDatabase().query(DescriptionTable.TABLE_NAME,
                null,
                "(" + DescriptionTable.Cols.DESCRIPTION + "=" + desc + ")",
                null,
                null,
                null,
                null);

        try {

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                mIdDescription = cursor.getInt(cursor.getColumnIndex(DescriptionTable.Cols.ID));
            }
        } finally {
            cursor.close();
        }
    }


    public void loadFromDB(int schedule) {
        TVScheduleDescriptionCursorWrapper cursor = queryDescription(getSQLDescription(schedule), null);

        try {

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                TVScheduleDescription description = cursor.getDescription();

                setIdSchedule(description.getIdSchedule());
                setIdDescription(description.getIdDescription());
                setDescription(description.getDescription());
                setImage(description.getImage());
                setActors(description.getActors());
                setDirectors(description.getDirectors());
                setGenres(description.getGenres());
                setCountry(description.getCountry());
                setYear(description.getYear());
                setRating(description.getRating());

            }
        } finally {
            cursor.close();
        }
    }

    private TVScheduleDescriptionCursorWrapper queryDescription(String sql, String[] selectionArgs) {
        Cursor cursor = mDataSource.getDatabase().rawQuery(sql, selectionArgs);
        return new TVScheduleDescriptionCursorWrapper(cursor);
    }

    public void saveToDB() {
        mDataSource.getDatabase().insert(DescriptionTable.TABLE_NAME, null, getContentProgramDescription());
    }

    private ContentValues getContentProgramDescription() {
        ContentValues values = new ContentValues();
        Log.d(TAG, "getContentProgramDescription - " + getDescription().replaceAll("'", "''"));
        values.put(DescriptionTable.Cols.DESCRIPTION, getDescription().replaceAll("'", "''"));
        values.put(DescriptionTable.Cols.IMAGE, getImage());
        values.put(DescriptionTable.Cols.GENRES, getGenres());
        values.put(DescriptionTable.Cols.ACTORS, getActors());
        values.put(DescriptionTable.Cols.DIRECTORS, getDirectors());
        values.put(DescriptionTable.Cols.COUNTRY, getCountry());
        values.put(DescriptionTable.Cols.YEAR, getYear());
        values.put(DescriptionTable.Cols.RATING, getRating());
        return values;
    }
}
