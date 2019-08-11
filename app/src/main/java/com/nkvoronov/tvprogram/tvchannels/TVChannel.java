package com.nkvoronov.tvprogram.tvchannels;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.Comparator;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.ALL_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;

public class TVChannel {
    public static final String SEPARATOR = ";";

    private int mIndex;
    private String mName;
    private String mIcon;
    private boolean mIsFavorites;
    private boolean mIsUpdate;
    private String mLang;
    private TVChannelsList mParent;

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
            String channelName1 = c1.getName().toUpperCase();
            String channelName2 = c2.getName().toUpperCase();
            return channelName1.compareTo(channelName2);
        }
    };

    public TVChannel(int index, String name, String icon) {
        mIndex = index;
        mName = name;
        mIcon = icon;
        mIsFavorites = false;
        mIsUpdate = false;
        mLang = ALL_LANG;
        mParent = null;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        Log.d(TAG, "LANG : " + lang);
        mLang = lang;
    }

    public boolean isFavorites() {
        return  mIsFavorites;
    }

    public boolean isUpdate() {
        return mIsUpdate;
    }

    public void setIsFavorites(boolean favorites) {
        mIsFavorites = favorites;
    }

    public void changeFavorites() {
        mParent.channelChangeFavorites(this);
        mIsFavorites = !mIsFavorites;
    }

    public void setIsUpdate(boolean update) {
        mIsUpdate = update;
    }

    public void setParent(TVChannelsList parent) {
        mParent = parent;
    }

    @Override
    public String toString() {
        return Integer.toString(getIndex()) + SEPARATOR + getName() + SEPARATOR + getIcon() + SEPARATOR + getLang() + SEPARATOR + Boolean.toString(isFavorites()) + SEPARATOR + Boolean.toString(isUpdate());
    }

    public void getXML(Document document, Element element) {
        Element xml_channel = document.createElement("channel");
        xml_channel.setAttribute("id", Integer.toString(getIndex()));
        Element xml_display_name = document.createElement("display-name");
        xml_display_name.setAttribute("lang", "ru");
        xml_display_name.appendChild(document.createTextNode(getName()));
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
