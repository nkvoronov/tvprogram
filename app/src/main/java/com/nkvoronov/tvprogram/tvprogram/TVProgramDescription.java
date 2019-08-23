package com.nkvoronov.tvprogram.tvprogram;

import android.database.Cursor;
import android.content.ContentValues;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import static com.nkvoronov.tvprogram.common.StringUtils.addQuotes;
import com.nkvoronov.tvprogram.database.TVProgramDescriptionCursorWrapper;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLDescription;

public class TVProgramDescription {
    private int mId;
    private String mYear;
    private String mImage;
    private int mIdProgram;
    private String mRating;
    private String mActors;
    private String mGenres;
    private String mCountry;
    private String mDirectors;
    private String mDescription;
    private String mUrlFullDesc;
    private String mShortDescription;
    private TVProgramDataSource mDataSource;

    public TVProgramDescription(String description) {
        mId = -1;
        mIdProgram = -1;
        mShortDescription = description;
        mDescription = null;
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

    public TVProgramDescription(int id, int program, String description) {
        mId = id;
        mIdProgram = program;
        mShortDescription = description;
        mDescription = null;
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

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public int getIdProgram() {
        return mIdProgram;
    }

    public void setIdProgram(int program) {
        mIdProgram = program;
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

    public String getShortDescription() {
        return mShortDescription;
    }

    public void setShortDescription(String description) {
        mShortDescription = description;
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

    public void setDataSource(TVProgramDataSource dataSource) {
        mDataSource = dataSource;
    }

    public void setIdFromDB() {
        mId = -1;
        String short_desc = addQuotes(getShortDescription(), "'");
        Cursor cursor = mDataSource.getDatabase().query(DescriptionTable.TABLE_NAME,
                null,
                "(" + DescriptionTable.Cols.SHORT_DESC + "=" + short_desc + ")",
                null,
                null,
                null,
                null);

        try {

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                mId = cursor.getInt(cursor.getColumnIndex(DescriptionTable.Cols.ID));
            }
        } finally {
            cursor.close();
        }
    }


    public void loadFromNet(int id) {
        TVProgramDescriptionCursorWrapper cursor = queryDescription(getSQLDescription(id), null);

        try {

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                TVProgramDescription description = cursor.getDescription();

                mId = description.getId();
                mShortDescription = description.getShortDescription();
                mDescription = description.getDescription();
                mYear = description.getYear();
                mImage = description.getImage();
                mActors = description.getActors();
                mGenres = description.getGenres();
                mCountry = description.getCountry();
                mDirectors = description.getDirectors();
                mRating = description.getRating();

            }
        } finally {
            cursor.close();
        }
    }

    private TVProgramDescriptionCursorWrapper queryDescription(String sql, String[] selectionArgs) {
        Cursor cursor = mDataSource.getDatabase().rawQuery(sql, selectionArgs);
        return new TVProgramDescriptionCursorWrapper(cursor);
    }

    public void saveToDB() {
        mDataSource.getDatabase().insert(DescriptionTable.TABLE_NAME, null, getContentProgramDescription());
    }

    private ContentValues getContentProgramDescription() {
        ContentValues values = new ContentValues();
        values.put(DescriptionTable.Cols.SHORT_DESC, getShortDescription());
        values.put(DescriptionTable.Cols.DESCRIPTION, getDescription());
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
