package com.nkvoronov.tvprogram.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ConfigsTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsFavTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsTvTable;

public class TVProgramBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "tvProgramBase.db";
    public static final String TAG = "BASE";

    public TVProgramBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        Log.d(TAG, "Create");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "ON Create");

        sqLiteDatabase.execSQL("CREATE TABLE " + ConfigsTable.TABLE_NAME + "(" +
                ConfigsTable.Cols.ID + " INTEGER  CONSTRAINT PK_CONF PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ConfigsTable.Cols.TYPE + " INTEGER NOT NULL DEFAULT (0), " +
                ConfigsTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ConfigsTable.Cols.VALUE + " VARCHAR DEFAULT (' ') " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_CONFIGS_ID ON " + ConfigsTable.TABLE_NAME + " (" + ConfigsTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CONFIGS_TYPE ON " + ConfigsTable.TABLE_NAME + " (" + ConfigsTable.Cols.TYPE + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CONFIGS_NAME ON " + ConfigsTable.TABLE_NAME + " (" + ConfigsTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsTable.TABLE_NAME + "(" +
                ChannelsTable.Cols.ID + " INTEGER  CONSTRAINT PK_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsTable.Cols.CHANNEL_INDEX + " INTEGER NOT NULL, " +
                ChannelsTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ChannelsTable.Cols.ICON + " VARCHAR, " +
                ChannelsTable.Cols.UPD_CHANNEL + " DATETIME DEFAULT (datetime('now')) " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_CHN_ID ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_INDEX ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL_INDEX + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_NAME ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsFavTable.TABLE_NAME + "(" +
                ChannelsFavTable.Cols.ID + " INTEGER  CONSTRAINT PK_FAV_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsFavTable.Cols.ID_CHANNEL + " INTEGER  CONSTRAINT FK_CHN_FAV REFERENCES channels (id) ON DELETE CASCADE NOT NULL, " +
                ChannelsFavTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ChannelsFavTable.Cols.CORRECTION + " INTEGER CONSTRAINT DF_USR_CHN DEFAULT (0), " +
                ChannelsFavTable.Cols.UPD_PROGRAM + " DATETIME DEFAULT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_FAV_CHN_ID ON " + ChannelsFavTable.TABLE_NAME + " (" + ChannelsFavTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_CHANNEL ON " + ChannelsFavTable.TABLE_NAME + " (" + ChannelsFavTable.Cols.ID_CHANNEL + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_NAME ON " + ChannelsFavTable.TABLE_NAME + " (" + ChannelsFavTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE VIEW " + ChannelsTvTable.TABLE_NAME + " AS SELECT " +
                "ac." + ChannelsTable.Cols.ID + " AS " + ChannelsTvTable.Cols.ID + ", " +
                "ac." + ChannelsTable.Cols.CHANNEL_INDEX+ " AS " + ChannelsTvTable.Cols.CHANNEL_INDEX + ", " +
                "ac." + ChannelsTable.Cols.NAME + " AS " + ChannelsTvTable.Cols.ORIGINAL_NAME + ", " +
                "CASE WHEN fc." + ChannelsFavTable.Cols.NAME + " IS NOT NULL THEN fc." + ChannelsFavTable.Cols.NAME + " ELSE \"none\" END AS " + ChannelsTvTable.Cols.USER_NAME + ", " +
                "ac." + ChannelsTable.Cols.ICON + " AS " + ChannelsTvTable.Cols.CHANNEL_ICON + ", " +
                "CASE WHEN fc." + ChannelsFavTable.Cols.CORRECTION + " IS NOT NULL THEN " + ChannelsFavTable.Cols.CORRECTION + " ELSE -1 END AS " + ChannelsTvTable.Cols.CORRECTION + ", " +
                "CASE WHEN fc." + ChannelsFavTable.Cols.CORRECTION + " IS NOT NULL THEN 1 ELSE 0 END AS " + ChannelsTvTable.Cols.FAVORITE + ", " +
                "CASE WHEN ( (julianday('now') - julianday(fc." + ChannelsFavTable.Cols.UPD_PROGRAM + ") ) > 7) OR (fc." + ChannelsFavTable.Cols.UPD_PROGRAM + " IS NULL) THEN 1 ELSE 0 END AS " + ChannelsTvTable.Cols.UPD_PROGRAM +
                " FROM " + ChannelsTable.TABLE_NAME + " ac LEFT JOIN " + ChannelsFavTable.TABLE_NAME + " fc ON (fc." + ChannelsFavTable.Cols.ID_CHANNEL + " = ac." + ChannelsTable.Cols.ID + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
