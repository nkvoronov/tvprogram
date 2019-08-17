package com.nkvoronov.tvprogram.tvchannels;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.database.TVChannelsAllCursorWrapper;
import com.nkvoronov.tvprogram.database.TVChannelsCursorWrapper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLChannels;
import static com.nkvoronov.tvprogram.database.TVProgramBaseHelper.getSQLFavoritesChannels;

public class TVChannelsList {
    private List<TVChannel> mData;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public TVChannelsList(Context context, SQLiteDatabase database) {
        mData = new ArrayList<>();
        mContext = context;
        mDatabase = database;
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

    public Context getContext() {
        return mContext;
    }

    public void loadFromDB(boolean isFavorites, int filter) {
        TVChannelsAllCursorWrapper cursor;
        mData.clear();

        if (isFavorites) {
            cursor = queryChannels(getSQLFavoritesChannels(TVProgramDataSource.get(mContext).getCoutDays()),null);
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
            mDatabase.delete(ChannelsFavoritesTable.TABLE_NAME, ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " = ?", new String[]{String.valueOf(channel.getIndex())});
        } else {
            mDatabase.insert(ChannelsFavoritesTable.TABLE_NAME, null, getContentChannelsFavValues(channel));
        }
    }

    private void insertChannel(TVChannel channel) {
        mDatabase.insert(ChannelsTable.TABLE_NAME, null, getContentChannelsAllValues(channel));
    }

    private void updateChannel(TVChannel channel) {
        mDatabase.update(ChannelsTable.TABLE_NAME, getContentChannelsAllValues(channel), ChannelsTable.Cols.CHANNEL_INDEX + " = ?", new String[]{String.valueOf(channel.getIndex())});
    }

    public void preUpdateChannel() {
        final ContentValues values = new ContentValues();
        values.putNull(ChannelsTable.Cols.UPD_CHANNEL);
        mDatabase.update(ChannelsTable.TABLE_NAME, values, null, null);
    }

    public void postUpdateChannel() {
        mDatabase.delete(ChannelsTable.TABLE_NAME, ChannelsTable.Cols.UPD_CHANNEL + " is null", null);
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
        values.put(ChannelsFavoritesTable.Cols.CHANNEL_INDEX, channel.getIndex());
        return values;
    }
}
