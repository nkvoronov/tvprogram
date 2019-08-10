package com.nkvoronov.tvprogram.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ConfigsTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsFavoritesTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsAllTable;

public class TVProgramBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "tvProgramBase.db";

    public TVProgramBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + ConfigsTable.TABLE_NAME + "(" +
                ConfigsTable.Cols.ID + " INTEGER PRIMARY KEY NOT NULL, " +
                ConfigsTable.Cols.COUNT_DAYS + " INTEGER NOT NULL DEFAULT (7), " +
                ConfigsTable.Cols.INDEX_SORT + " INTEGER NOT NULL DEFAULT (1) " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsTable.TABLE_NAME + "(" +
                ChannelsTable.Cols.ID + " INTEGER CONSTRAINT PK_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsTable.Cols.CHANNEL_INDEX + " INTEGER NOT NULL, " +
                ChannelsTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ChannelsTable.Cols.ICON + " VARCHAR, " +
                ChannelsTable.Cols.LANG + " VARCHAR, " +
                ChannelsTable.Cols.UPD_CHANNEL + " DATETIME DEFAULT (datetime('now')) " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_CHN_ID ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_INDEX ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL_INDEX + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_NAME ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsFavoritesTable.TABLE_NAME + "(" +
                ChannelsFavoritesTable.Cols.ID + " INTEGER CONSTRAINT PK_FAV_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " INTEGER CONSTRAINT FK_CHN_FAV REFERENCES " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL_INDEX + ") ON DELETE CASCADE NOT NULL, " +
                ChannelsFavoritesTable.Cols.UPD_PROGRAM + " DATETIME DEFAULT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_FAV_CHN_ID ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_CHANNEL ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " ASC)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static String getSQLChannels() {
        String sql =
                "SELECT " +
                "ca." + ChannelsTable.Cols.CHANNEL_INDEX + " AS " + ChannelsAllTable.Cols.CHANNEL_INDEX + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + ChannelsAllTable.Cols.CHANNEL_NAME + ", " +
                "ca." + ChannelsTable.Cols.ICON + " AS " + ChannelsAllTable.Cols.CHANNEL_ICON + ", " +
                "ca." + ChannelsTable.Cols.LANG + " AS " + ChannelsAllTable.Cols.LANG + ", " +
                "CASE WHEN (SELECT cf." + ChannelsFavoritesTable.Cols.ID + " FROM " + ChannelsFavoritesTable.TABLE_NAME + " cf WHERE cf." + ChannelsFavoritesTable.Cols.CHANNEL_INDEX + "=ca." + ChannelsTable.Cols.CHANNEL_INDEX + ") IS NULL THEN 0 ELSE 1 END AS " + ChannelsAllTable.Cols.FAVORITE + ", " +
                "0 AS " +  ChannelsAllTable.Cols.UPD_PROGRAM + " " +
                "FROM " +
                ChannelsTable.TABLE_NAME + " ca " +
                "ORDER BY " +
                ChannelsTable.Cols.CHANNEL_INDEX;
                //"WHERE " + ChannelsAllTable.Cols.LANG + "=? " + !!!!!!!!!!!
        return sql;
    }

    public static String getSQLFavoritesChannels(int countDay) {
        String sql =
                "SELECT " +
                "ca." + ChannelsTable.Cols.CHANNEL_INDEX + " AS " + ChannelsAllTable.Cols.CHANNEL_INDEX + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + ChannelsAllTable.Cols.CHANNEL_NAME + ", " +
                "ca." + ChannelsTable.Cols.ICON + " AS " + ChannelsAllTable.Cols.CHANNEL_ICON + ", " +
                "ca." + ChannelsTable.Cols.LANG + " AS " + ChannelsAllTable.Cols.LANG + ", " +
                "1 AS " + ChannelsAllTable.Cols.FAVORITE + ", " +
                "CASE WHEN ((JULIANDAY('now')-JULIANDAY(cf."+ ChannelsFavoritesTable.Cols.UPD_PROGRAM +"))>" + Integer.toString(countDay) +") OR (cf." + ChannelsFavoritesTable.Cols.UPD_PROGRAM + " IS NULL) THEN 1 ELSE 0 END AS " + ChannelsAllTable.Cols.UPD_PROGRAM + " " +
                "FROM " +
                ChannelsFavoritesTable.TABLE_NAME + " cf " +
                "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (cf." + ChannelsFavoritesTable.Cols.CHANNEL_INDEX + "=ca." + ChannelsTable.Cols.CHANNEL_INDEX + ") " +
                "ORDER BY " +
                "ca." + ChannelsTable.Cols.NAME;
        return sql;
    }
}
