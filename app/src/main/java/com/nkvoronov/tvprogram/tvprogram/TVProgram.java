package com.nkvoronov.tvprogram.tvprogram;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TVProgram {
    public static final String SEPARATOR = ";";
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

    @Override
    public String toString() {
        return Integer.toString(getIndex()) + SEPARATOR + getStart().toString() + SEPARATOR + getStop().toString() + SEPARATOR + getTitle() + SEPARATOR + getDescription();
    }

    private String getDateToFormat(Date date) {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMddHHmmss");
        String result = date_format.format(date);
        if (mCorrectionTime != null) {
            result = result + " " + mCorrectionTime;
        }
        return result;
    }

    public void getXML(Document document, Element element) {
        Element xml_program = document.createElement("programme");
        xml_program.setAttribute("start", getDateToFormat(getStart()));
        xml_program.setAttribute("stop", getDateToFormat(getStop()));
        xml_program.setAttribute("channel", Integer.toString(getIndex()));
        Element xml_title = document.createElement("title");
        xml_title.setAttribute("lang", "ru");
        xml_title.appendChild(document.createTextNode(getTitle()));
        xml_program.appendChild(xml_title);
        if (getDescription() != null) {
            Element xml_description = document.createElement("desc");
            xml_description.setAttribute("lang", "ru");
            xml_description.appendChild(document.createTextNode(getDescription()));
            xml_program.appendChild(xml_description);
        }
        if (getDirectors() != null ||  getActors() != null) {
            Element xml_credits = document.createElement("credits");
            if (getDirectors() != null) {
                String[] xml_str_list = getDirectors().split(SEPARATOR_LIST);
                for (String str:xml_str_list) {
                    Element xml_director = document.createElement("director");
                    xml_director.appendChild(document.createTextNode(str));
                    xml_credits.appendChild(xml_director);
                }
            }
            if (getActors() != null) {
                String[] xml_str_list = getActors().split(SEPARATOR_LIST);
                for (String str:xml_str_list) {
                    Element xml_actor = document.createElement("actor");
                    xml_actor.appendChild(document.createTextNode(str));
                    xml_credits.appendChild(xml_actor);
                }
            }
            xml_program.appendChild(xml_credits);
        }
        if (getDate() != null) {
            Element xml_date = document.createElement("date");
            xml_date.appendChild(document.createTextNode(getDate()));
            xml_program.appendChild(xml_date);
        }
        if (getCategory() != null) {
            Element xml_category2 = document.createElement("category");
            xml_category2.setAttribute("lang", "ru");
            xml_category2.appendChild(document.createTextNode(getCategory()));
            xml_program.appendChild(xml_category2);
        }
        if (getGenres() != null) {
            String[] xml_str_list = getGenres().split(SEPARATOR_LIST);
            for (String str:xml_str_list) {
                Element xml_category3 = document.createElement("category");
                xml_category3.setAttribute("lang", "ru");
                xml_category3.appendChild(document.createTextNode(str));
                xml_program.appendChild(xml_category3);
            }
        }
        if (getStarRating() != null) {
            Element xml_rating = document.createElement("star-rating");
            Element xml_value = document.createElement("value");
            xml_value.appendChild(document.createTextNode(getStarRating()));
            xml_rating.appendChild(xml_value);
            xml_program.appendChild(xml_rating);
        }
        element.appendChild(xml_program);
    }
}
