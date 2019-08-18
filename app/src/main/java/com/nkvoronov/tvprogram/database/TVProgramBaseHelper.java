package com.nkvoronov.tvprogram.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ConfigsTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsFavoritesTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.SchedulesTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsAllTable;
import java.util.Date;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import static com.nkvoronov.tvprogram.common.StringUtils.*;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.RUS_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.UKR_LANG;

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
                ConfigsTable.Cols.COUNT_DAYS + " INTEGER NOT NULL DEFAULT (7) " +
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
                ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " INTEGER CONSTRAINT FK_CHN_FAV REFERENCES " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL_INDEX + ") ON DELETE CASCADE NOT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_FAV_CHN_ID ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_CHANNEL ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.CHANNEL_INDEX + " ASC)");


        sqLiteDatabase.execSQL("CREATE TABLE " + SchedulesTable.TABLE_NAME + "(" +
                SchedulesTable.Cols.ID + " INTEGER CONSTRAINT PK_SCH PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SchedulesTable.Cols.CHANNEL + " INTEGER CONSTRAINT FK_CHN_SCH REFERENCES " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL_INDEX + ") ON DELETE CASCADE NOT NULL, " +
                SchedulesTable.Cols.CATEGORY + " INTEGER DEFAULT (0), " +
                SchedulesTable.Cols.START + " DATETIME, " +
                SchedulesTable.Cols.END + " DATETIME, " +
                SchedulesTable.Cols.TITLE + " VARCHAR " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_SCH_ID ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_CHANNEL ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.CHANNEL + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_CATEGORY ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.CATEGORY + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_START ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.START + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_END ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.END + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_TITLE ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.TITLE + " ASC)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static String getSQLChannels(int filter) {
        String sql_filter = "";
        if (filter == 1) {
            sql_filter = "WHERE ca." + ChannelsTable.Cols.LANG + "=" + addQuotes(RUS_LANG, "'") + " ";
        }
        if (filter == 2) {
            sql_filter = "WHERE ca." + ChannelsTable.Cols.LANG + "=" + addQuotes(UKR_LANG, "'") + " ";
        }

        String sql =
                "SELECT " +
                "ca." + ChannelsTable.Cols.CHANNEL_INDEX + " AS " + ChannelsAllTable.Cols.CHANNEL_INDEX + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + ChannelsAllTable.Cols.CHANNEL_NAME + ", " +
                "ca." + ChannelsTable.Cols.ICON + " AS " + ChannelsAllTable.Cols.CHANNEL_ICON + ", " +
                "ca." + ChannelsTable.Cols.LANG + " AS " + ChannelsAllTable.Cols.LANG + ", " +
                "CASE WHEN (SELECT cf." + ChannelsFavoritesTable.Cols.ID + " FROM " + ChannelsFavoritesTable.TABLE_NAME + " cf WHERE cf." + ChannelsFavoritesTable.Cols.CHANNEL_INDEX + "=ca." + ChannelsTable.Cols.CHANNEL_INDEX + ") IS NULL THEN 0 ELSE 1 END AS " + ChannelsAllTable.Cols.FAVORITE + " " +
                "FROM " +
                ChannelsTable.TABLE_NAME + " ca " +
                sql_filter +
                "ORDER BY " +
                "ca." + ChannelsTable.Cols.CHANNEL_INDEX;
        Log.d(TAG, sql);
        return sql;
    }

    public static String getSQLFavoritesChannels(int countDay) {
        String sql =
                "SELECT " +
                "ca." + ChannelsTable.Cols.CHANNEL_INDEX + " AS " + ChannelsAllTable.Cols.CHANNEL_INDEX + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + ChannelsAllTable.Cols.CHANNEL_NAME + ", " +
                "ca." + ChannelsTable.Cols.ICON + " AS " + ChannelsAllTable.Cols.CHANNEL_ICON + ", " +
                "ca." + ChannelsTable.Cols.LANG + " AS " + ChannelsAllTable.Cols.LANG + ", " +
                "1 AS " + ChannelsAllTable.Cols.FAVORITE + " " +
                "FROM " +
                ChannelsFavoritesTable.TABLE_NAME + " cf " +
                "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (cf." + ChannelsFavoritesTable.Cols.CHANNEL_INDEX + "=ca." + ChannelsTable.Cols.CHANNEL_INDEX + ") " +
                "ORDER BY " +
                "ca." + ChannelsTable.Cols.NAME;
        return sql;
    }

    public static String getSQLProgramsForChannelToDate(int channel, Date date) {
        String sql_filter = "";
        String sChannel = String.valueOf(channel);
        sql_filter = "WHERE (" + SchedulesTable.Cols.CHANNEL + "=" + sChannel + ")";
        if (date != null) {
            String sDate1 = addQuotes(getDateFormat(date, "yyyy-MM-dd"), "'");
            String sDate2 = addQuotes(getDateFormat(addDays(date, 1), "yyyy-MM-dd"), "'");
            sql_filter = sql_filter + " and ((" + SchedulesTable.Cols.START + ">=" + sDate1 + ") and (" + SchedulesTable.Cols.START + "<" + sDate2 + "))";
        }

        String sql =
                "SELECT " +
                SchedulesTable.Cols.ID + ", " +
                SchedulesTable.Cols.CHANNEL + ", " +
                SchedulesTable.Cols.CATEGORY + ", " +
                SchedulesTable.Cols.START + ", " +
                SchedulesTable.Cols.END + ", " +
                SchedulesTable.Cols.TITLE + " " +
                "FROM " +
                SchedulesTable.TABLE_NAME + " " +
                sql_filter + " " +
                "ORDER BY " +
                SchedulesTable.Cols.START;
        Log.d(TAG, sql);
        return sql;
    }
}
