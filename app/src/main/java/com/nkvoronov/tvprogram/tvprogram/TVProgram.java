package com.nkvoronov.tvprogram.tvprogram;

import java.util.Date;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVProgram {
    private int mId;
    private int mIndex;
    private Date mStop;
    private Date mStart;
    private String mYear;
    private int mCategory;
    private String mTitle;
    private String mImage;
    private int mTimeType;
    private String mActors;
    private String mGenres;
    private String mCountry;
    private String mDirectors;
    private String mStarRating;
    private String mNameChannel;
    private String mDescription;
    private String mUrlFullDesc;
    private boolean mIsFavorites;
    private TVProgramDataSource mDataSource;

    public TVProgram(int id, int index, Date start, Date stop, String title) {
        mId = id;
        mIndex = index;
        mNameChannel = null;
        mStart = start;
        mStop = stop;
        mTitle = title;
        mCategory = 0;
        mGenres = null;
        mCountry = null;
        mYear = null;
        mDescription = null;
        mUrlFullDesc = null;
        mDirectors = null;
        mActors = null;
        mImage = null;
        mStarRating = null;
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

    public int getCategory() {
        return mCategory;
    }

    public void setCategory(int category) {
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

    public String getYear() {
        return mYear;
    }

    public void setYear(String year) {
        mYear = year;
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

    public boolean isFavorites() {
        return mIsFavorites;
    }

    public void setFavorites(boolean favorites) {
        this.mIsFavorites = favorites;
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
}
