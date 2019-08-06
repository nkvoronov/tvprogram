package com.nkvoronov.tvprogram.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.nkvoronov.tvprogram.common.Channel;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsTvTable;

public class TvChannelsCursorWrapper extends CursorWrapper {

    public TvChannelsCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Channel getChannel() {
        int id = getInt(getColumnIndex(ChannelsTvTable.Cols.ID));
        int index = getInt(getColumnIndex(ChannelsTvTable.Cols.CHANNEL_INDEX));
        String original_name = getString(getColumnIndex(ChannelsTvTable.Cols.ORIGINAL_NAME));
        String user_name = getString(getColumnIndex(ChannelsTvTable.Cols.USER_NAME));
        String icon = getString(getColumnIndex(ChannelsTvTable.Cols.CHANNEL_ICON));
        int correction = getInt(getColumnIndex(ChannelsTvTable.Cols.CORRECTION));
        int favorite = getInt(getColumnIndex(ChannelsTvTable.Cols.FAVORITE));
        int upd_program = getInt(getColumnIndex(ChannelsTvTable.Cols.UPD_PROGRAM));

        Channel channel = new Channel(id, index, original_name, user_name, icon, correction);
        channel.setIsFav(favorite == 1);
        channel.setIsUpd(upd_program == 1);

        return channel;
    }
}
