package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsTvTable;

public class TVChannelsAllCursorWrapper extends CursorWrapper {

    public TVChannelsAllCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public TVChannel getChannel() {
        int index = getInt(getColumnIndex(ChannelsTvTable.Cols.CHANNEL_INDEX));
        String original_name = getString(getColumnIndex(ChannelsTvTable.Cols.ORIGINAL_NAME));
        String user_name = getString(getColumnIndex(ChannelsTvTable.Cols.USER_NAME));
        String icon = getString(getColumnIndex(ChannelsTvTable.Cols.CHANNEL_ICON));
        int correction = getInt(getColumnIndex(ChannelsTvTable.Cols.CORRECTION));
        int favorite = getInt(getColumnIndex(ChannelsTvTable.Cols.FAVORITE));
        int upd_program = getInt(getColumnIndex(ChannelsTvTable.Cols.UPD_PROGRAM));

        TVChannel channel = new TVChannel(index, original_name, user_name, icon, correction);
        channel.setIsFav(favorite == 1);
        channel.setIsUpd(upd_program == 1);

        return channel;
    }
}
