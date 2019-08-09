package com.nkvoronov.tvprogram.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ConfigsTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsAllTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsFavTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsTvTable;

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

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsAllTable.TABLE_NAME + "(" +
                ChannelsAllTable.Cols.ID + " INTEGER CONSTRAINT PK_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsAllTable.Cols.CHANNEL_INDEX + " INTEGER NOT NULL, " +
                ChannelsAllTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ChannelsAllTable.Cols.ICON + " VARCHAR, " +
                ChannelsAllTable.Cols.UPD_CHANNEL + " DATETIME DEFAULT (datetime('now')) " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_CHN_ID ON " + ChannelsAllTable.TABLE_NAME + " (" + ChannelsAllTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_INDEX ON " + ChannelsAllTable.TABLE_NAME + " (" + ChannelsAllTable.Cols.CHANNEL_INDEX + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_NAME ON " + ChannelsAllTable.TABLE_NAME + " (" + ChannelsAllTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsFavTable.TABLE_NAME + "(" +
                ChannelsFavTable.Cols.ID + " INTEGER CONSTRAINT PK_FAV_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsFavTable.Cols.CHANNEL_INDEX + " INTEGER CONSTRAINT FK_CHN_FAV REFERENCES " + ChannelsAllTable.TABLE_NAME + " (" + ChannelsAllTable.Cols.CHANNEL_INDEX + ") ON DELETE CASCADE NOT NULL, " +
                ChannelsFavTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ChannelsFavTable.Cols.CORRECTION + " INTEGER CONSTRAINT DF_USR_CHN DEFAULT (0), " +
                ChannelsFavTable.Cols.UPD_PROGRAM + " DATETIME DEFAULT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_FAV_CHN_ID ON " + ChannelsFavTable.TABLE_NAME + " (" + ChannelsFavTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_CHANNEL ON " + ChannelsFavTable.TABLE_NAME + " (" + ChannelsFavTable.Cols.CHANNEL_INDEX + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_NAME ON " + ChannelsFavTable.TABLE_NAME + " (" + ChannelsFavTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE VIEW " + ChannelsTvTable.TABLE_NAME + " AS SELECT " +
                "ac." + ChannelsAllTable.Cols.ID + " AS " + ChannelsTvTable.Cols.ID + ", " +
                "ac." + ChannelsAllTable.Cols.CHANNEL_INDEX+ " AS " + ChannelsTvTable.Cols.CHANNEL_INDEX + ", " +
                "ac." + ChannelsAllTable.Cols.NAME + " AS " + ChannelsTvTable.Cols.ORIGINAL_NAME + ", " +
                "CASE WHEN fc." + ChannelsFavTable.Cols.NAME + " IS NOT NULL THEN fc." + ChannelsFavTable.Cols.NAME + " ELSE \"none\" END AS " + ChannelsTvTable.Cols.USER_NAME + ", " +
                "ac." + ChannelsAllTable.Cols.ICON + " AS " + ChannelsTvTable.Cols.CHANNEL_ICON + ", " +
                "CASE WHEN fc." + ChannelsFavTable.Cols.CORRECTION + " IS NOT NULL THEN " + ChannelsFavTable.Cols.CORRECTION + " ELSE -1 END AS " + ChannelsTvTable.Cols.CORRECTION + ", " +
                "CASE WHEN fc." + ChannelsFavTable.Cols.CORRECTION + " IS NOT NULL THEN 1 ELSE 0 END AS " + ChannelsTvTable.Cols.FAVORITE + ", " +
                "CASE WHEN ( (julianday('now') - julianday(fc." + ChannelsFavTable.Cols.UPD_PROGRAM + ") ) > 7) OR (fc." + ChannelsFavTable.Cols.UPD_PROGRAM + " IS NULL) THEN 1 ELSE 0 END AS " + ChannelsTvTable.Cols.UPD_PROGRAM +
                " FROM " + ChannelsAllTable.TABLE_NAME + " ac LEFT JOIN " + ChannelsFavTable.TABLE_NAME + " fc ON (fc." + ChannelsFavTable.Cols.CHANNEL_INDEX + " = ac." + ChannelsAllTable.Cols.CHANNEL_INDEX + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
