package com.nkvoronov.tvprogram.common;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.Comparator;
import java.util.UUID;

public class Channel {
    public static final String SEPARATOR = ";";

    private UUID mId;
    private int mIndex;
    private String mUName;
    private String mOName;
    private String mIcon;
    private int mCorrection;

    public static Comparator<Channel> channelIndexComparator = new Comparator<Channel>() {
        @Override
        public int compare(Channel c1, Channel c2) {
            int channelIndex1 = c1.getIndex();
            int channelIndex2 = c2.getIndex();
            return channelIndex1 - channelIndex2;
        }
    };

    public static Comparator<Channel> channelNameComparator = new Comparator<Channel>() {
        @Override
        public int compare(Channel c1, Channel c2) {
            String channelName1 = c1.getOName().toUpperCase();
            String channelName2 = c2.getOName().toUpperCase();
            return channelName1.compareTo(channelName2);
        }
    };

    public Channel(UUID id, int index, String oName, String uName, String icon, int correction) {
        mId = id;
        mIndex = index;
        mOName = oName;
        mUName = uName;
        mIcon = icon;
        mCorrection = correction;
    }

    public Channel(int index, String oName, String uName, String icon, int correction) {
        this(UUID.randomUUID(), index, oName, uName, icon, correction);
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = mId;
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

    @Override
    public String toString() {
        return Integer.toString(getIndex()) + SEPARATOR + getOName() + SEPARATOR + getUName() + SEPARATOR + getIcon() + SEPARATOR + Integer.toString(getCorrection());
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
