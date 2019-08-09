package com.nkvoronov.tvprogram.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.nkvoronov.tvprogram.database.TVChannelsCursorWrapper;
import com.nkvoronov.tvprogram.database.ConfigCursorWrapper;
import com.nkvoronov.tvprogram.database.TVProgramBaseHelper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import com.nkvoronov.tvprogram.database.TVChannelsAllCursorWrapper;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import static com.nkvoronov.tvprogram.common.HttpContent.CHANNELS_PRE;
import static com.nkvoronov.tvprogram.common.HttpContent.ICONS_PRE;

public class TVProgramDataSource {
    private static final String DEF_ID = "1";
    private static final int DEF_CORRECTION = 120;
    private static final String CHANNELS_SELECT = "option[value^=channel_]";

    public static final String RUS_LANG = "rus";
    public static final String UKR_LANG = "ukr";
    public static final String ALL_LANG = "all";

    public static final String TAG = "TVPROGRAM";

    private static TVProgramDataSource sTVProgramLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    private int mCoutDays;
    private String mLang;
    private boolean mIndexSort;

    public TVProgramDataSource(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TVProgramBaseHelper(mContext).getWritableDatabase();
        initConfig();
    }

    private void initConfig() {
        mCoutDays = 7;
        mLang = RUS_LANG;
        mIndexSort = true;
        Log.d(TAG, "initConfig");
        ConfigCursorWrapper cursor = new ConfigCursorWrapper(mDatabase.query(
                ConfigsTable.TABLE_NAME,
                null,
                ConfigsTable.Cols.ID + " = ?",
                new String[]{ DEF_ID },
                null,
                null,
                null
                ));
        try {
            if (cursor.getCount() == 0) {
                mDatabase.insert(ConfigsTable.TABLE_NAME, null, getConfigValues(true));
                Log.d(TAG, "initConfig insert");
            } else {
                cursor.moveToFirst();
                mCoutDays = cursor.getCountDays();
                mLang = cursor.getLang();
                mIndexSort = cursor.getIndexSort();
                Log.d(TAG, "initConfig get");
            }
        } finally {
            cursor.close();
        }

    }

    public static TVProgramDataSource get(Context context) {
        if (sTVProgramLab == null) {
            sTVProgramLab = new TVProgramDataSource(context);
        }
        return sTVProgramLab;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public int getCoutDays() {
        return mCoutDays;
    }

    public void setCoutDays(int coutDays) {
        mCoutDays = coutDays;
        updateConfig();
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        mLang = lang;
        updateConfig();
    }

    public boolean isIndexSort() {
        return mIndexSort;
    }

    public void setIndexSort(boolean indexSort) {
        mIndexSort = indexSort;
        updateConfig();
    }

    public List<TVChannel> getChannelsFromNetAndUpdate(boolean isUpdate) {
        List<TVChannel> channels = new ArrayList<>();
        String channel_index;
        String channel_name;
        String channel_link;
        String channel_icon;
        Boolean flag = false;

        org.jsoup.nodes.Document document = new HttpContent(CHANNELS_PRE).getDocument();
        org.jsoup.select.Elements elements = document.select(CHANNELS_SELECT);
        for (org.jsoup.nodes.Element element : elements){
            channel_name = element.text();
            flag = channel_name.endsWith("(на укр.)");
            if ((!flag && mLang.equals(RUS_LANG))||(flag && mLang.equals(UKR_LANG))) {
                channel_link = element.attr("value");
                channel_index = channel_link.split("_")[1];
                channel_icon = ICONS_PRE + channel_index + ".gif";
                TVChannel channel = new TVChannel(Integer.parseInt(channel_index), channel_name, channel_name, channel_icon, DEF_CORRECTION);
                channel.setIsFav(false);
                channel.setIsUpd(false);
                channels.add(channel);
                Log.d(TAG, channel.toString());
                if (isUpdate) {
                    saveChannelToDB(channel);
                    saveChannelIcon(channel);
                }
            }
        }
        if (mIndexSort) {
            Collections.sort(channels, TVChannel.channelIndexComparator);
        } else {
            Collections.sort(channels, TVChannel.channelNameComparator);
        }
        return channels;
    }

    public List<TVChannel> getChannels(boolean isFavorites) {
        List<TVChannel> channels = new ArrayList<>();
        String selection = null;
        String[] selectionArg = null;
        String orderBy = ChannelsTvTable.Cols.CHANNEL_INDEX;

        if (isFavorites) {
            selection = ChannelsTvTable.Cols.FAVORITE + " = ?";
            selectionArg = new String[]{ "1" };
            orderBy = ChannelsTvTable.Cols.USER_NAME;
        }

        TVChannelsAllCursorWrapper cursor = new TVChannelsAllCursorWrapper(mDatabase.query(
                ChannelsTvTable.TABLE_NAME,
                null,
                selection,
                selectionArg,
                null,
                null,
                orderBy
        ));

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                channels.add(cursor.getChannel());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return channels;
    }

    public void saveChannelToDB(TVChannel channel) {
        int typeUpdate = 0;
        String oldName = "";

        TVChannelsCursorWrapper cursor = new TVChannelsCursorWrapper(mDatabase.query(
                ChannelsAllTable.TABLE_NAME,
                null,
                ChannelsAllTable.Cols.CHANNEL_INDEX + " = ?",
                new String[]{String.valueOf(channel.getIndex())},
                null,
                null,
                null
        ));
        Log.d(TAG, "saveChannelToDB : " + String.valueOf(channel.getIndex()));
        try {
            if (cursor.getCount() != 0) {
                typeUpdate = 0;
                cursor.moveToFirst();
                oldName = cursor.getName();
                if (!channel.getOName().equals(oldName)) {
                    typeUpdate = 1;
                }
                Log.d(TAG, "saveChannelToDB update");
            } else {
                typeUpdate = 2;
                Log.d(TAG, "saveChannelToDB insert");
            }
        } finally {
            cursor.close();
        }
        if (typeUpdate == 1) {
            updateChannel(channel);
        }
        if (typeUpdate == 2) {
            insertChannel(channel);
        }
    }

    public void addChannelToFavorites(TVChannel channel) {
        //
    }

    public void delChannelFromFavorites(TVChannel channel) {
        //
    }

    public void saveChannelIcon(TVChannel channel) {
        //
    }

    public String ChannelsToString(List<TVChannel> channels) {
        String result = "";
        for (TVChannel channel : channels) {
            result += channel.toString() + "\n";
        }
        return result;
    }

    public List<TVChannel> loadChannelsFromFile(String fileName) {
        List<TVChannel> channels = new ArrayList<>();
        File file = new File(fileName);
        try {
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                    e.fillInStackTrace();
                }
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] aline = line.split(TVChannel.SEPARATOR);
                TVChannel channel = new TVChannel(Integer.parseInt(aline[0]), aline[1], aline[2], aline[3], Integer.parseInt(aline[4]));
                channel.setIsFav(false);
                channel.setIsUpd(false);
                channels.add(channel);
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.fillInStackTrace();
        }
        return channels;
    }

    public void saveChannelsToFile(List<TVChannel> channels, String fileName) {
        File file = new File(fileName);
        try {
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                    e.fillInStackTrace();
                }
            }
            PrintWriter printWriter = new PrintWriter(file.getAbsoluteFile());
            for (TVChannel channel : channels) {
                printWriter.println(channel.toString());
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public void getChannelsToXML(List<TVChannel> channels, Document document, Element element) {
        for (TVChannel channel : channels) {
            channel.getXML(document, element);
        }
    }

    private void updateConfig() {
        mDatabase.update(ConfigsTable.TABLE_NAME, getConfigValues(false), ConfigsTable.Cols.ID + " = ?", new String[]{ DEF_ID });
    }

    private void insertChannel(TVChannel channel) {
        mDatabase.insert(ChannelsAllTable.TABLE_NAME, null, getContentChannelsAllValues(channel));
    }

    private void updateChannel(TVChannel channel) {
        mDatabase.update(ChannelsAllTable.TABLE_NAME, getContentChannelsAllValues(channel), ChannelsAllTable.Cols.CHANNEL_INDEX + " = ?", new String[]{String.valueOf(channel.getIndex())});
    }

    private ContentValues getConfigValues(boolean withID) {
        ContentValues values = new ContentValues();
        if (withID) {
            values.put(ConfigsTable.Cols.ID, DEF_ID);
        }
        values.put(ConfigsTable.Cols.COUNT_DAYS, getCoutDays());
        values.put(ConfigsTable.Cols.LANG, getLang());
        values.put(ConfigsTable.Cols.INDEX_SORT, isIndexSort() ? 1 : 0);
        return values;
    }

    private ContentValues getContentChannelsAllValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        Date date = new Date();
        values.put(ChannelsAllTable.Cols.CHANNEL_INDEX, channel.getIndex());
        values.put(ChannelsAllTable.Cols.NAME, channel.getOName());
        values.put(ChannelsAllTable.Cols.ICON, channel.getIcon());
        values.put(ChannelsAllTable.Cols.UPD_CHANNEL, date.getTime());
        return values;
    }

    private ContentValues getContentChannelsFavValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        values.put(ChannelsFavTable.Cols.CHANNEL_INDEX, channel.getIndex());
        values.put(ChannelsFavTable.Cols.NAME, channel.getUName());
        values.put(ChannelsFavTable.Cols.CORRECTION, channel.getCorrection());
        return values;
    }
}
