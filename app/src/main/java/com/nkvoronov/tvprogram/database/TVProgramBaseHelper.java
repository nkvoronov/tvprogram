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
                ConfigsTable.Cols.LANG + " VARCHAR NOT NULL DEFAULT ('ru'), " +
                ConfigsTable.Cols.INDEX_SORT + " INTEGER NOT NULL DEFAULT (1) " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsTable.TABLE_NAME + "(" +
                ChannelsTable.Cols.ID + " INTEGER CONSTRAINT PK_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsTable.Cols.CHANNEL_INDEX + " INTEGER NOT NULL, " +
                ChannelsTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ChannelsTable.Cols.ICON + " VARCHAR, " +
                ChannelsTable.Cols.UPD_CHANNEL + " DATETIME DEFAULT (datetime('now')) " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_CHN_ID ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_INDEX ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL_INDEX + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_NAME ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsFavoritesTable.TABLE_NAME + "(" +
                ChannelsFavoritesTable.Cols.ID + " INTEGER CONSTRAINT PK_FAV_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " INTEGER CONSTRAINT FK_CHN_FAV REFERENCES " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL_INDEX + ") ON DELETE CASCADE NOT NULL, " +
                ChannelsFavoritesTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ChannelsFavoritesTable.Cols.CORRECTION + " INTEGER CONSTRAINT DF_USR_CHN DEFAULT (0), " +
                ChannelsFavoritesTable.Cols.UPD_PROGRAM + " DATETIME DEFAULT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_FAV_CHN_ID ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_CHANNEL ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_NAME ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE VIEW " + ChannelsAllTable.TABLE_NAME + " AS SELECT " +
                "ac." + ChannelsTable.Cols.ID + " AS " + ChannelsAllTable.Cols.ID + ", " +
                "ac." + ChannelsTable.Cols.CHANNEL_INDEX+ " AS " + ChannelsAllTable.Cols.CHANNEL_INDEX + ", " +
                "ac." + ChannelsTable.Cols.NAME + " AS " + ChannelsAllTable.Cols.ORIGINAL_NAME + ", " +
                "CASE WHEN fc." + ChannelsFavoritesTable.Cols.NAME + " IS NOT NULL THEN fc." + ChannelsFavoritesTable.Cols.NAME + " ELSE \"none\" END AS " + ChannelsAllTable.Cols.USER_NAME + ", " +
                "ac." + ChannelsTable.Cols.ICON + " AS " + ChannelsAllTable.Cols.CHANNEL_ICON + ", " +
                "CASE WHEN fc." + ChannelsFavoritesTable.Cols.CORRECTION + " IS NOT NULL THEN " + ChannelsFavoritesTable.Cols.CORRECTION + " ELSE -1 END AS " + ChannelsAllTable.Cols.CORRECTION + ", " +
                "CASE WHEN fc." + ChannelsFavoritesTable.Cols.CORRECTION + " IS NOT NULL THEN 1 ELSE 0 END AS " + ChannelsAllTable.Cols.FAVORITE + ", " +
                "CASE WHEN ( (julianday('now') - julianday(fc." + ChannelsFavoritesTable.Cols.UPD_PROGRAM + ") ) > 7) OR (fc." + ChannelsFavoritesTable.Cols.UPD_PROGRAM + " IS NULL) THEN 1 ELSE 0 END AS " + ChannelsAllTable.Cols.UPD_PROGRAM +
                " FROM " + ChannelsTable.TABLE_NAME + " ac LEFT JOIN " + ChannelsFavoritesTable.TABLE_NAME + " fc ON (fc." + ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " = ac." + ChannelsTable.Cols.CHANNEL_INDEX + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
