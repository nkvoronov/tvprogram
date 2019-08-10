package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsAllTable;

public class TVChannelsAllCursorWrapper extends CursorWrapper {

    public TVChannelsAllCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVChannel getChannel() {
        int index = getInt(getColumnIndex(ChannelsAllTable.Cols.CHANNEL_INDEX));
        String original_name = getString(getColumnIndex(ChannelsAllTable.Cols.ORIGINAL_NAME));
        String user_name = getString(getColumnIndex(ChannelsAllTable.Cols.USER_NAME));
        String icon = getString(getColumnIndex(ChannelsAllTable.Cols.CHANNEL_ICON));
        int correction = getInt(getColumnIndex(ChannelsAllTable.Cols.CORRECTION));
        int favorite = getInt(getColumnIndex(ChannelsAllTable.Cols.FAVORITE));
        int upd_program = getInt(getColumnIndex(ChannelsAllTable.Cols.UPD_PROGRAM));

        TVChannel channel = new TVChannel(index, original_name, user_name, icon, correction);
        channel.setIsFavorites(favorite == 1);
        channel.setIsUpdate(upd_program == 1);

        return channel;
    }
}
