package com.nkvoronov.tvprogram.tvschedule;

import java.io.File;
import android.database.Cursor;
import android.content.ContentValues;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import com.nkvoronov.tvprogram.database.TVScheduleDescriptionCursorWrapper;
import static com.nkvoronov.tvprogram.database.MainBaseHelper.getSQLDescription;

public class TVScheduleDescription {
    private String mYear;
    private String mType;
    private String mImage;
    private int mIdCatalog;
    private String mRating;
    private String mActors;
    private String mGenres;
    private int mIdSchedule;
    private String mCountry;
    private String mDirectors;
    private int mIdDescription;
    private String mDescription;
    private String mTitle;
    private MainDataSource mDataSource;

    public TVScheduleDescription(String description) {
        this(-1, -1, description);
    }

    public TVScheduleDescription(int id, int schedule, String description) {
        mIdDescription = id;
        mIdSchedule = schedule;
        mDescription = description;
        mImage = null;
        mGenres = null;
        mDirectors = null;
        mActors = null;
        mCountry = null;
        mYear = null;
        mRating = null;
        mType = null;
        mTitle = null;
        mIdCatalog = 0;
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

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public int getIdCatalog() {
        return mIdCatalog;
    }

    public void setIdCatalog(int idCatalog) {
        mIdCatalog = idCatalog;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDataSource(MainDataSource dataSource) {
        mDataSource = dataSource;
    }

    public void setIdFromDB() {
        Cursor cursor = null;
        setIdDescription(-1);
        if (getType() == null || getIdCatalog() == 0) {
            String desc = getDescription();
            cursor = mDataSource.getDatabase().query(DescriptionTable.TABLE_NAME,
                    null,
                    DescriptionTable.Cols.DESCRIPTION + "=?",
                    new String[]{desc},
                    null,
                    null,
                    null);
        } else {
            String type = getType();
            String catalog = String.valueOf(getIdCatalog());
            cursor = mDataSource.getDatabase().query(DescriptionTable.TABLE_NAME,
                    null,
                    "(" + DescriptionTable.Cols.TYPE + "=?) AND (" + DescriptionTable.Cols.CATALOG + "=?)",
                    new String[]{type, catalog},
                    null,
                    null,
                    null);
        }

        try {

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                setIdDescription(cursor.getInt(cursor.getColumnIndex(DescriptionTable.Cols.ID)));
            }
        } finally {
            cursor.close();
        }
    }


    public void loadFromDB() {
        TVScheduleDescriptionCursorWrapper cursor = queryDescription(getSQLDescription(getIdSchedule()), null);

        try {
            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                TVScheduleDescription description = cursor.getDescription();

                setIdSchedule(description.getIdSchedule());
                setIdDescription(description.getIdDescription());
                setTitle(description.getTitle());
                setDescription(description.getDescription());
                setImage(description.getImage());
                setActors(description.getActors());
                setDirectors(description.getDirectors());
                setGenres(description.getGenres());
                setCountry(description.getCountry());
                setYear(description.getYear());
                setRating(description.getRating());
                setType(description.getType());
                setIdCatalog(description.getIdCatalog());
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
        mDataSource.getDatabase().insert(DescriptionTable.TABLE_NAME, null, getContentScheduleDescription());
    }

    public void updateDB() {
        mDataSource.getDatabase().update(DescriptionTable.TABLE_NAME, getContentScheduleDescription(), DescriptionTable.Cols.ID + "=?", new String[]{String.valueOf(getIdDescription())});
    }

    private ContentValues getContentScheduleDescription() {
        ContentValues values = new ContentValues();
        values.put(DescriptionTable.Cols.TITLE, getTitle());
        values.put(DescriptionTable.Cols.DESCRIPTION, getDescription());
        values.put(DescriptionTable.Cols.IMAGE, getImage());
        values.put(DescriptionTable.Cols.GENRES, getGenres());
        values.put(DescriptionTable.Cols.ACTORS, getActors());
        values.put(DescriptionTable.Cols.DIRECTORS, getDirectors());
        values.put(DescriptionTable.Cols.COUNTRY, getCountry());
        values.put(DescriptionTable.Cols.YEAR, getYear());
        values.put(DescriptionTable.Cols.RATING, getRating());
        values.put(DescriptionTable.Cols.TYPE, getType());
        values.put(DescriptionTable.Cols.CATALOG, getIdCatalog());
        return values;
    }

    public void saveImageToFile() {
        File imageFile = mDataSource.getDescriptionImageFile(getType(), getIdCatalog());
        mDataSource.saveFileFromNet(imageFile, getImage());
    }
}
