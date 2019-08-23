package com.nkvoronov.tvprogram.tvchannels;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import java.text.SimpleDateFormat;
import android.content.ContentValues;
import android.database.DatabaseUtils;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import com.nkvoronov.tvprogram.database.TVChannelsCursorWrapper;
import com.nkvoronov.tvprogram.database.TVChannelsNameCursorWrapper;
import static com.nkvoronov.tvprogram.database.MainBaseHelper.getSQLAllChannels;
import static com.nkvoronov.tvprogram.database.MainBaseHelper.getSQLFavoritesChannels;

public class TVChannelsList {
    private List<TVChannel> mData;
    private MainDataSource mDataSource;

    public TVChannelsList(MainDataSource dataSource) {
        mData = new ArrayList<>();
        mDataSource = dataSource;
    }

    public List<TVChannel> getData() {
        return mData;
    }

    public TVChannel get(int position) {
        return getData().get(position);
    }

    public TVChannel getForIndex(int index) {
        TVChannel channel = null;
        for (int i = 0; i < size(); i++) {
            if (get(i).getIndex() == index) {
                channel = get(i);
                break;
            }
        }
        return channel;
    }

    public int size() {
        return getData().size();
    }

    public void clear() {
        getData().clear();
    }

    public void add(TVChannel channel) {
        channel.setDataSource(mDataSource);
        getData().add(channel);
    }

    public void loadFromDB(boolean isFavorites, int filter) {
        TVChannelsCursorWrapper cursor;
        clear();

        if (isFavorites) {
            cursor = queryChannels(getSQLFavoritesChannels(mDataSource.getCoutDays()),null);
        } else {
            cursor = queryChannels(getSQLAllChannels(filter),null);
        }

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                TVChannel channel = cursor.getChannel();
                add(channel);
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    private TVChannelsCursorWrapper queryChannels(String sql, String[] selectionArgs) {
        Cursor cursor = mDataSource.getDatabase().rawQuery(sql, selectionArgs);
        return new TVChannelsCursorWrapper(cursor);
    }

    public void saveChannelToDB(TVChannel channel) {
        int typeUpdate = 0;
        String oldName = "";

        TVChannelsNameCursorWrapper cursor = new TVChannelsNameCursorWrapper(mDataSource.getDatabase().query(
                ChannelsTable.TABLE_NAME,
                null,
                ChannelsTable.Cols.CHANNEL + " = ?",
                new String[]{String.valueOf(channel.getIndex())},
                null,
                null,
                null
        ));
        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                typeUpdate = 1;
            } else {
                typeUpdate = 2;
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

    private void insertChannel(TVChannel channel) {
        mDataSource.getDatabase().insert(ChannelsTable.TABLE_NAME, null, getContentChannelsAllValues(channel));
    }

    private void updateChannel(TVChannel channel) {
        mDataSource.getDatabase().update(ChannelsTable.TABLE_NAME, getContentChannelsAllValues(channel), ChannelsTable.Cols.CHANNEL + " = ?", new String[]{String.valueOf(channel.getIndex())});
    }

    public void preUpdateChannel() {
        final ContentValues values = new ContentValues();
        values.putNull(ChannelsTable.Cols.UPDATED);
        mDataSource.getDatabase().update(ChannelsTable.TABLE_NAME, values, null, null);
    }

    public void postUpdateChannel() {
        mDataSource.getDatabase().delete(ChannelsTable.TABLE_NAME, ChannelsTable.Cols.UPDATED + " is null", null);
    }

    private ContentValues getContentChannelsAllValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        values.put(ChannelsTable.Cols.CHANNEL, channel.getIndex());
        values.put(ChannelsTable.Cols.NAME, channel.getName());
        values.put(ChannelsTable.Cols.ICON, channel.getIcon());
        values.put(ChannelsTable.Cols.LANG, channel.getLang());
        values.put(ChannelsTable.Cols.UPDATED, simpleDateFormat.format(date));
        return values;
    }
}
