package com.nkvoronov.tvprogram.tvchannels;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.database.TVChannelsAllCursorWrapper;
import com.nkvoronov.tvprogram.database.TVChannelsCursorWrapper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import static com.nkvoronov.tvprogram.common.HttpContent.CHANNELS_PRE;
import static com.nkvoronov.tvprogram.common.HttpContent.ICONS_PRE;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.ALL_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.RUS_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.UKR_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLChannels;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLFavoritesChannels;

public class TVChannelsList {
    private static final int DEF_CORRECTION = 120;
    private static final String CHANNELS_SELECT = "option[value^=channel_]";

    private Boolean mIndexSort;
    private List<TVChannel> mData;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public TVChannelsList(Context context, SQLiteDatabase database, boolean indexSort) {
        mIndexSort = indexSort;
        mData = new ArrayList<>();
        mContext = context;
        mDatabase = database;
    }

    public Boolean isIndexSort() {
        return mIndexSort;
    }

    public void setIndexSort(Boolean indexSort) {
        mIndexSort = indexSort;
    }

    public List<TVChannel> getData() {
        return mData;
    }

    public TVChannel get(int position) {
        return mData.get(position);
    }

    public int size() {
        return mData.size();
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public String toString() {
        String result = "";
        for (TVChannel channel : getData()) {
            result += channel.toString() + "\n";
        }
        return result;
    }

    public void loadFromNetAndUpdate(boolean isUpdate) {
        String channel_index;
        String channel_name;
        String channel_link;
        String channel_icon;
        String lang = ALL_LANG;
        Boolean flag = false;
        mData.clear();
        org.jsoup.nodes.Document document = new HttpContent(CHANNELS_PRE).getDocument();
        org.jsoup.select.Elements elements = document.select(CHANNELS_SELECT);
        for (org.jsoup.nodes.Element element : elements){
            channel_name = element.text();
            if (channel_name.endsWith("(на укр.)")) {
                lang = UKR_LANG;
            } else {
                lang = RUS_LANG;
            }
            channel_link = element.attr("value");
            channel_index = channel_link.split("_")[1];
            channel_icon = ICONS_PRE + channel_index + ".gif";
            TVChannel channel = new TVChannel(Integer.parseInt(channel_index), channel_name, channel_icon);
            channel.setLang(lang);
            channel.setIsFavorites(false);
            channel.setIsUpdate(false);
            mData.add(channel);
            channel.setParent(this);
            Log.d(TAG, channel.toString());
            if (isUpdate) {
                saveChannelToDB(channel);
                channel.saveIconToFile();
            }

        }
        if (mIndexSort) {
            Collections.sort(mData, TVChannel.channelIndexComparator);
        } else {
            Collections.sort(mData, TVChannel.channelNameComparator);
        }
    }

    public void loadFromDB(boolean isFavorites, int filter) {
        TVChannelsAllCursorWrapper cursor;
        mData.clear();

        if (isFavorites) {
            cursor = queryChannels(getSQLFavoritesChannels(TVProgramDataSource.get(null).getCoutDays()),null);
            Log.d(TAG, "FAV");
        } else {
            cursor = queryChannels(getSQLChannels(filter),null);
            Log.d(TAG, "ALL");
        }

        try {
            Log.d(TAG, Integer.toString(cursor.getCount()));
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TVChannel channel = cursor.getChannel();
                mData.add(channel);
                channel.setParent(this);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    private TVChannelsAllCursorWrapper queryChannels(String sql, String[] selectionArgs) {
        Cursor cursor = mDatabase.rawQuery(sql, selectionArgs);
        return new TVChannelsAllCursorWrapper(cursor);
    }

    public void loadFromFile(String fileName) {
        mData.clear();
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
                TVChannel channel = new TVChannel(Integer.parseInt(aline[0]), aline[1], aline[2]);
                channel.setLang(aline[3]);
                channel.setIsFavorites(Boolean.getBoolean(aline[4]));
                channel.setIsUpdate(Boolean.getBoolean(aline[5]));
                mData.add(channel);
                channel.setParent(this);
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.fillInStackTrace();
        }
    }

    public void saveToFile(String fileName) {
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
            for (TVChannel channel : getData()) {
                printWriter.println(channel.toString());
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public void getXML(Document document, Element element) {
        for (TVChannel channel : getData()) {
            channel.getXML(document, element);
        }
    }

    public void saveChannelToDB(TVChannel channel) {
        int typeUpdate = 0;
        String oldName = "";

        TVChannelsCursorWrapper cursor = new TVChannelsCursorWrapper(mDatabase.query(
                ChannelsTable.TABLE_NAME,
                null,
                ChannelsTable.Cols.CHANNEL_INDEX + " = ?",
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
                if (!channel.getName().equals(oldName)) {
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

    public void channelChangeFavorites(TVChannel channel) {
        if (channel.isFavorites()) {
            mDatabase.delete(TVProgramDbSchema.ChannelsFavoritesTable.TABLE_NAME, TVProgramDbSchema.ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " = ?", new String[]{String.valueOf(channel.getIndex())});
        } else {
            mDatabase.insert(TVProgramDbSchema.ChannelsFavoritesTable.TABLE_NAME, null, getContentChannelsFavValues(channel));
        }
    }

    private void insertChannel(TVChannel channel) {
        mDatabase.insert(ChannelsTable.TABLE_NAME, null, getContentChannelsAllValues(channel));
    }

    private void updateChannel(TVChannel channel) {
        mDatabase.update(ChannelsTable.TABLE_NAME, getContentChannelsAllValues(channel), ChannelsTable.Cols.CHANNEL_INDEX + " = ?", new String[]{String.valueOf(channel.getIndex())});
    }


    private ContentValues getContentChannelsAllValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        Date date = new Date();
        values.put(ChannelsTable.Cols.CHANNEL_INDEX, channel.getIndex());
        values.put(ChannelsTable.Cols.NAME, channel.getName());
        values.put(ChannelsTable.Cols.ICON, channel.getIcon());
        values.put(ChannelsTable.Cols.LANG, channel.getLang());
        values.put(ChannelsTable.Cols.UPD_CHANNEL, date.getTime());
        return values;
    }

    private ContentValues getContentChannelsFavValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        values.put(TVProgramDbSchema.ChannelsFavoritesTable.Cols.CHANNEL_INDEX, channel.getIndex());
        return values;
    }
}
