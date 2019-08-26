package com.nkvoronov.tvprogram.tvchannels;

import java.io.File;
import android.util.Log;
import com.nkvoronov.tvprogram.common.MainDataSource;
import static com.nkvoronov.tvprogram.common.MainDataSource.TAG;
import static com.nkvoronov.tvprogram.common.MainDataSource.ALL_LANG;

public class TVChannel {
    private int mIndex;
    private String mName;
    private String mIcon;
    private String mLang;
    private boolean mIsFavorites;
    private MainDataSource mDataSource;

    public TVChannel(int index, String name, String icon) {
        mIndex = index;
        mName = name;
        mIcon = icon;
        mIsFavorites = false;
        mLang = ALL_LANG;
        mDataSource = null;
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
        mLang = lang;
    }

    public boolean isFavorites() {
        return  mIsFavorites;
    }

    public void setDataSource(MainDataSource dataSource) {
        mDataSource = dataSource;
    }

    public void saveIconToFile() {
        File iconFile = mDataSource.getChannelIconFile(getIndex());
        mDataSource.saveFileFromNet(iconFile, getIcon());
    }

    public void setIsFavorites(boolean favorites) {
        mIsFavorites = favorites;
    }

    public void changeFavorites() {
        mDataSource.channelChangeFavorites(this);
        mIsFavorites = !mIsFavorites;
    }
}
