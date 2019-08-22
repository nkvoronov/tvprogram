package com.nkvoronov.tvprogram.tvprogram;

import com.nkvoronov.tvprogram.common.TVProgramDataSource;

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

    public void loadFromNet() {
        //
    }
}
