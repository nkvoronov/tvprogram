package com.nkvoronov.tvprogram.database;

import java.util.Date;
import android.util.Log;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import com.nkvoronov.tvprogram.tvprogram.TVProgramCategory;
import static com.nkvoronov.tvprogram.common.StringUtils.*;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.RUS_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.UKR_LANG;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsFavoritesTable;
import static com.nkvoronov.tvprogram.tvprogram.TVProgramCategoriesList.getContentProgramCategoryValues;

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
                ChannelsTable.Cols.CHANNEL + " INTEGER NOT NULL, " +
                ChannelsTable.Cols.NAME + " VARCHAR NOT NULL, " +
                ChannelsTable.Cols.ICON + " VARCHAR, " +
                ChannelsTable.Cols.LANG + " VARCHAR, " +
                ChannelsTable.Cols.UPDATED + " DATETIME DEFAULT (datetime('now')) " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_CHN_ID ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_INDEX ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CHN_NAME ON " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.NAME + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ChannelsFavoritesTable.TABLE_NAME + "(" +
                ChannelsFavoritesTable.Cols.ID + " INTEGER CONSTRAINT PK_FAV_CHN PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ChannelsFavoritesTable.Cols.CHANNEL + " INTEGER CONSTRAINT FK_CHN_FAV REFERENCES " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL + ") ON DELETE CASCADE NOT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_FAV_CHN_ID ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_FAV_CHN_CHANNEL ON " + ChannelsFavoritesTable.TABLE_NAME + " (" + ChannelsFavoritesTable.Cols.CHANNEL + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + CategoryTable.TABLE_NAME + "(" +
                CategoryTable.Cols.ID + " INTEGER CONSTRAINT PK_CAT PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                CategoryTable.Cols.NAME + " VARCHAR NOT NULL, " +
                CategoryTable.Cols.DICTIONARY + " VARCHAR, " +
                CategoryTable.Cols.COLOR + " VARCHAR(6) " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_CAT_ID ON " + CategoryTable.TABLE_NAME + " (" + CategoryTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CAT_NAME ON " + CategoryTable.TABLE_NAME + " (" + CategoryTable.Cols.NAME + " ASC)");

        insertDataCategories(sqLiteDatabase);

        sqLiteDatabase.execSQL("CREATE TABLE " + SchedulesTable.TABLE_NAME + "(" +
                SchedulesTable.Cols.ID + " INTEGER CONSTRAINT PK_SCH PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SchedulesTable.Cols.CHANNEL + " INTEGER CONSTRAINT FK_CHN_SCH REFERENCES " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL + ") ON DELETE CASCADE NOT NULL, " +
                SchedulesTable.Cols.CATEGORY + " INTEGER DEFAULT (0) CONSTRAINT FK_CAT_SCH REFERENCES " + CategoryTable.TABLE_NAME + " (" + CategoryTable.Cols.ID + ") ON DELETE CASCADE NOT NULL , " +
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

        sqLiteDatabase.execSQL("CREATE TABLE " + SchedulesFavoritesTable.TABLE_NAME + "(" +
                SchedulesFavoritesTable.Cols.ID + " INTEGER CONSTRAINT PK_SCH_FAV PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SchedulesFavoritesTable.Cols.SCHEDULE + " INTEGER CONSTRAINT FK_CHN_SCH REFERENCES " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.ID + ") ON DELETE CASCADE NOT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_SCH_FAV_ID ON " + SchedulesFavoritesTable.TABLE_NAME + " (" + SchedulesFavoritesTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_FAV_SCH ON " + SchedulesFavoritesTable.TABLE_NAME + " (" + SchedulesFavoritesTable.Cols.SCHEDULE + " ASC)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }

    private void insertDataCategories(SQLiteDatabase database) {
        database.insert(CategoryTable.TABLE_NAME, null, getContentProgramCategoryValues(new TVProgramCategory(0, "Без категории", "", "000000")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentProgramCategoryValues(new TVProgramCategory(1, "Фильм", "х/ф,д/ф", "0000FF")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentProgramCategoryValues(new TVProgramCategory(2, "Сериал", "т/с,х/с,д/с", "1E90FF")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentProgramCategoryValues(new TVProgramCategory(3, "Мультфильм", "м/с,м/ф,мульт", "9400D3")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentProgramCategoryValues(new TVProgramCategory(4, "Спорт", "спорт,футбол,хокей,uefa", "008000")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentProgramCategoryValues(new TVProgramCategory(5, "Новости", "новост,факты,тсн,новини,время,известия", "DC143C")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentProgramCategoryValues(new TVProgramCategory(6, "Досуг", "истори,планет,разрушители,знаки,катастроф", "B8860B")));
    }

    public static String getSQLAllChannels(int filter) {
        String sql_filter = "";
        if (filter == 1) {
            sql_filter = "WHERE ca." + ChannelsTable.Cols.LANG + "=" + addQuotes(RUS_LANG, "'") + " ";
        }
        if (filter == 2) {
            sql_filter = "WHERE ca." + ChannelsTable.Cols.LANG + "=" + addQuotes(UKR_LANG, "'") + " ";
        }

        String sql =
                "SELECT " +
                "ca." + ChannelsTable.Cols.CHANNEL + " AS " + ChannelsTable.Cols.CHANNEL + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + ChannelsTable.Cols.NAME + ", " +
                "ca." + ChannelsTable.Cols.ICON + " AS " + ChannelsTable.Cols.ICON + ", " +
                "ca." + ChannelsTable.Cols.LANG + " AS " + ChannelsTable.Cols.LANG + ", " +
                "CASE WHEN (SELECT cf." + ChannelsFavoritesTable.Cols.ID + " FROM " + ChannelsFavoritesTable.TABLE_NAME + " cf WHERE cf." + ChannelsFavoritesTable.Cols.CHANNEL + "=ca." + ChannelsTable.Cols.CHANNEL + ") IS NULL THEN 0 ELSE 1 END AS " + ChannelsTable.Cols.FAVORITE + " " +
                "FROM " +
                ChannelsTable.TABLE_NAME + " ca " +
                sql_filter +
                "ORDER BY " +
                "ca." + ChannelsTable.Cols.CHANNEL;
        Log.d(TAG, sql);
        return sql;
    }

    public static String getSQLFavoritesChannels(int countDay) {
        String sql =
                "SELECT " +
                "ca." + ChannelsTable.Cols.CHANNEL + " AS " + ChannelsTable.Cols.CHANNEL + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + ChannelsTable.Cols.NAME + ", " +
                "ca." + ChannelsTable.Cols.ICON + " AS " + ChannelsTable.Cols.ICON + ", " +
                "ca." + ChannelsTable.Cols.LANG + " AS " + ChannelsTable.Cols.LANG + ", " +
                "1 AS " + ChannelsTable.Cols.FAVORITE + " " +
                "FROM " +
                ChannelsFavoritesTable.TABLE_NAME + " cf " +
                "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (cf." + ChannelsFavoritesTable.Cols.CHANNEL + "=ca." + ChannelsTable.Cols.CHANNEL + ") " +
                "ORDER BY " +
                "ca." + ChannelsTable.Cols.NAME;
        return sql;
    }

    public static String getSQLProgramsForChannelToDay(int filter, Date date) {
        String sql_filter = "";
        String channel = String.valueOf(filter);
        sql_filter = "WHERE (sch." + SchedulesTable.Cols.CHANNEL + "=" + channel + ")";
        if (date != null) {
            String sDate1 = addQuotes(getDateFormat(date, "yyyy-MM-dd"), "'");
            String sDate2 = addQuotes(getDateFormat(addDays(date, 1), "yyyy-MM-dd"), "'");
            sql_filter = sql_filter + " and ((sch." + SchedulesTable.Cols.START + ">=" + sDate1 + ") AND (sch." + SchedulesTable.Cols.START + "<" + sDate2 + "))";
        }

        String sql =
                "SELECT " +
                "sch." + SchedulesTable.Cols.ID + " AS " + SchedulesTable.Cols.ID + ", " +
                "sch." + SchedulesTable.Cols.CHANNEL + " AS " + SchedulesTable.Cols.CHANNEL + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + SchedulesTable.Cols.NAME + ", " +
                "sch." + SchedulesTable.Cols.CATEGORY + " AS " + SchedulesTable.Cols.CATEGORY + ", " +
                "sch." + SchedulesTable.Cols.START + " AS " + SchedulesTable.Cols.START + ", " +
                "sch." + SchedulesTable.Cols.END + " AS " + SchedulesTable.Cols.END + ", " +
                "sch." + SchedulesTable.Cols.TITLE + " AS " + SchedulesTable.Cols.TITLE + ", " +
                "CASE WHEN (sch." + SchedulesTable.Cols.START + "<=datetime('now','localtime')) AND (sch." + SchedulesTable.Cols.END + ">=datetime('now','localtime')) THEN 1 WHEN sch." + SchedulesTable.Cols.START + "<=datetime('now','localtime') THEN 0 else 2 END AS " + SchedulesTable.Cols.TIME_TYPE + ", " +
                "CASE WHEN (SELECT sf." + SchedulesFavoritesTable.Cols.ID + " FROM " + SchedulesFavoritesTable.TABLE_NAME + " sf WHERE sf." + SchedulesFavoritesTable.Cols.ID + "=sch." + SchedulesTable.Cols.ID + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.FAVORITE + " " +
                "FROM " +
                SchedulesTable.TABLE_NAME + " sch " +
                "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (sch." + SchedulesTable.Cols.CHANNEL + "=ca." + ChannelsTable.Cols.CHANNEL + ") " +
                sql_filter + " " +
                "ORDER BY " +
                "sch." + SchedulesTable.Cols.START;
        Log.d(TAG, sql);
        return sql;
    }

    public static String getSQLNowPrograms(int filter) {
        String sql_filter = "";
        String category = String.valueOf(filter);
        if (filter > 0) {
            sql_filter = "AND (sch." + SchedulesTable.Cols.CATEGORY + "=" + category + ")";
        }
        String sql =
                "SELECT " +
                        "sch." + SchedulesTable.Cols.ID + " AS " + SchedulesTable.Cols.ID + ", " +
                        "sch." + SchedulesTable.Cols.CHANNEL + " AS " + SchedulesTable.Cols.CHANNEL + ", " +
                        "ca." + ChannelsTable.Cols.NAME + " AS " + SchedulesTable.Cols.NAME + ", " +
                        "sch." + SchedulesTable.Cols.CATEGORY + " AS " + SchedulesTable.Cols.CATEGORY + ", " +
                        "sch." + SchedulesTable.Cols.START + " AS " + SchedulesTable.Cols.START + ", " +
                        "sch." + SchedulesTable.Cols.END + " AS " + SchedulesTable.Cols.END + ", " +
                        "sch." + SchedulesTable.Cols.TITLE + " AS " + SchedulesTable.Cols.TITLE + ", " +
                        "1 AS " + SchedulesTable.Cols.TIME_TYPE + ", " +
                        "CASE WHEN (SELECT sf." + SchedulesFavoritesTable.Cols.ID + " FROM " + SchedulesFavoritesTable.TABLE_NAME + " sf WHERE sf." + SchedulesFavoritesTable.Cols.ID + "=sch." + SchedulesTable.Cols.ID + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.FAVORITE + " " +
                        "FROM " +
                        SchedulesTable.TABLE_NAME + " sch " +
                        "JOIN " + ChannelsFavoritesTable.TABLE_NAME + " cf ON (sch." + SchedulesTable.Cols.CHANNEL + "=cf." + ChannelsFavoritesTable.Cols.CHANNEL + ") " +
                        "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (cf." + ChannelsFavoritesTable.Cols.CHANNEL + "=ca." + ChannelsTable.Cols.CHANNEL + ") " +
                        "WHERE (sch." + SchedulesTable.Cols.START + "<=datetime('now','localtime')) AND (sch." + SchedulesTable.Cols.END + ">datetime('now','localtime')) " +
                        sql_filter + " " +
                        "ORDER BY " +
                        "ca." + ChannelsTable.Cols.NAME;
        Log.d(TAG, sql);
        return sql;
    }
}
