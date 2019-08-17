package com.nkvoronov.tvprogram.tvchannels;

import android.util.Log;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.ALL_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;

public class TVChannel {
    private int mIndex;
    private String mName;
    private String mIcon;
    private boolean mIsFavorites;
    private String mLang;
    private TVChannelsList mParent;

    public TVChannel(int index, String name, String icon) {
        mIndex = index;
        mName = name;
        mIcon = icon;
        mIsFavorites = false;
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
        mLang = lang;
    }

    public boolean isFavorites() {
        return  mIsFavorites;
    }

    public File getIconFile() {
        File filesDir = mParent.getContext().getFilesDir();
        return new File(filesDir, "IMG_" + Integer.toString(getIndex()) + ".gif");
    }

    public void saveIconToFile() {
        try {
            if (!getIconFile().exists()) {
                URL url = new URL(getIcon());
                InputStream inputStream = url.openStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(getIconFile());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = dataInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());
            e.fillInStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.fillInStackTrace();
        }
    }

    public void setIsFavorites(boolean favorites) {
        mIsFavorites = favorites;
    }

    public void changeFavorites() {
        mParent.channelChangeFavorites(this);
        mIsFavorites = !mIsFavorites;
    }

    public void setParent(TVChannelsList parent) {
        mParent = parent;
    }
}
