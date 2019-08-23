package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.database.MainDbSchema.ChannelsTable;

public class TVChannelsCursorWrapper extends CursorWrapper {

    public TVChannelsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVChannel getChannel() {
        int index = getInt(getColumnIndex(ChannelsTable.Cols.CHANNEL));
        String name = getString(getColumnIndex(ChannelsTable.Cols.NAME));
        String icon = getString(getColumnIndex(ChannelsTable.Cols.ICON));
        String lang = getString(getColumnIndex(ChannelsTable.Cols.LANG));
        int favorite = getInt(getColumnIndex(ChannelsTable.Cols.FAVORITE));

        TVChannel channel = new TVChannel(index, name, icon);
        channel.setLang(lang);
        channel.setIsFavorites(favorite == 1);

        return channel;
    }
}
