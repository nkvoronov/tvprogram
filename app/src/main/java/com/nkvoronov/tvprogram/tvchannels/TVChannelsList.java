package com.nkvoronov.tvprogram.tvchannels;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
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
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLChannels;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLFavoritesChannels;

public class TVChannelsList {
    private static final int DEF_CORRECTION = 120;

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

    public TVChannel getForIndex(int index) {
        TVChannel channel = null;
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getIndex() == index) {
                channel = mData.get(i);
                break;
            }
        }
        return channel;
    }

    public int size() {
        return mData.size();
    }

    public void clear() {
        mData.clear();
    }

    public void add(TVChannel channel) {
        mData.add(channel);
    }

    public void sort(boolean indexSort) {
        if (mIndexSort) {
            Collections.sort(mData, TVChannel.channelIndexComparator);
        } else {
            Collections.sort(mData, TVChannel.channelNameComparator);
        }
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
                cursor.moveToFirst();
                typeUpdate = 1;
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
        values.put(ChannelsTable.Cols.UPD_CHANNEL, date.toString());
        return values;
    }

    private ContentValues getContentChannelsFavValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        values.put(TVProgramDbSchema.ChannelsFavoritesTable.Cols.CHANNEL_INDEX, channel.getIndex());
        return values;
    }
}
