package com.nkvoronov.tvprogram.tvchannels;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.Comparator;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.ALL_LANG;

public class TVChannel {
    public static final String SEPARATOR = ";";

    private int mIndex;
    private String mUName;
    private String mOName;
    private String mIcon;
    private boolean mIsFav;
    private boolean mIsUpd;
    private int mCorrection;
    private String mLang;

    public static Comparator<TVChannel> channelIndexComparator = new Comparator<TVChannel>() {
        @Override
        public int compare(TVChannel c1, TVChannel c2) {
            int channelIndex1 = c1.getIndex();
            int channelIndex2 = c2.getIndex();
            return channelIndex1 - channelIndex2;
        }
    };

    public static Comparator<TVChannel> channelNameComparator = new Comparator<TVChannel>() {
        @Override
        public int compare(TVChannel c1, TVChannel c2) {
            String channelName1 = c1.getOName().toUpperCase();
            String channelName2 = c2.getOName().toUpperCase();
            return channelName1.compareTo(channelName2);
        }
    };

    public TVChannel(int index, String oName, String uName, String icon, int correction) {
        mIndex = index;
        mOName = oName;
        mUName = uName;
        mIcon = icon;
        mIsFav = false;
        mIsUpd = false;
        mCorrection = correction;
        mLang = ALL_LANG;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public String getUName() {
        return mUName;
    }

    public void setUName(String uName) {
        mUName = uName;
    }

    public String getOName() {
        return mOName;
    }

    public void setOName(String oName) {
        mOName = oName;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public int getCorrection() {
        return mCorrection;
    }

    public void setCorrection(int correction) {
        mCorrection = correction;
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String mLang) {
        this.mLang = mLang;
    }

    public boolean isFav() {
        return mIsFav;
    }

    public boolean isUpd() {
        return mIsUpd;
    }

    public void setIsFav(boolean fav) {
        mIsFav = fav;
    }

    public void setIsUpd(boolean upd) {
        mIsUpd = upd;
    }

    @Override
    public String toString() {
        return Integer.toString(getIndex()) + SEPARATOR + getOName() + SEPARATOR + getUName() + SEPARATOR + getIcon() + SEPARATOR + Integer.toString(getCorrection()) + SEPARATOR + getLang() + SEPARATOR + Boolean.toString(isFav()) + SEPARATOR + Boolean.toString(isUpd());
    }

    public void getXML(Document document, Element element) {
        Element xml_channel = document.createElement("channel");
        xml_channel.setAttribute("id", Integer.toString(getIndex()));
        Element xml_display_name = document.createElement("display-name");
        xml_display_name.setAttribute("lang", "ru");
        if (getUName().equals("None")) {
            xml_display_name.appendChild(document.createTextNode(getOName()));
        } else {
            xml_display_name.appendChild(document.createTextNode(getUName()));
        }
        xml_channel.appendChild(xml_display_name);
        if (!getIcon().equals("")) {
            Element xml_icon_link = document.createElement("icon");
            xml_icon_link.setAttribute("src", getIcon());
            xml_display_name.appendChild(xml_icon_link);
        }
        element.appendChild(xml_display_name);
    }

    public String getIconFilename() {
        return "";
    }
}
