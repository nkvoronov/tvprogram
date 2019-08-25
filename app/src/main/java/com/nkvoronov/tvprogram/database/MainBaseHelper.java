package com.nkvoronov.tvprogram.database;

import java.util.Date;
import android.util.Log;
import android.content.Context;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleCategory;
import static com.nkvoronov.tvprogram.common.MainDataSource.TAG;
import static com.nkvoronov.tvprogram.common.MainDataSource.RUS_LANG;
import static com.nkvoronov.tvprogram.common.MainDataSource.UKR_LANG;
import com.nkvoronov.tvprogram.database.MainDbSchema.ChannelsFavoritesTable;
import static com.nkvoronov.tvprogram.tvschedule.TVScheduleCategoriesList.getContentScheduleCategoryValues;

public class MainBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "tvProgramBase.db";

    public MainBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + ConfigsTable.TABLE_NAME + "(" +
                ConfigsTable.Cols.ID + " INTEGER PRIMARY KEY NOT NULL, " +
                ConfigsTable.Cols.COUNT_DAYS + " INTEGER NOT NULL DEFAULT (7), " +
                ConfigsTable.Cols.FULL_DESC + " INTEGER NOT NULL DEFAULT (0) " +
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
                CategoryTable.Cols.DICTIONARY + " VARCHAR " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_CAT_ID ON " + CategoryTable.TABLE_NAME + " (" + CategoryTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_CAT_NAME ON " + CategoryTable.TABLE_NAME + " (" + CategoryTable.Cols.NAME + " ASC)");

        insertDataCategories(sqLiteDatabase);

        sqLiteDatabase.execSQL("CREATE TABLE " + DescriptionTable.TABLE_NAME + "(" +
                DescriptionTable.Cols.ID + " INTEGER CONSTRAINT PK_DESCRIPTION PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                DescriptionTable.Cols.DESCRIPTION + " VARCHAR, " +
                DescriptionTable.Cols.IMAGE + " VARCHAR , " +
                DescriptionTable.Cols.GENRES + " VARCHAR, " +
                DescriptionTable.Cols.DIRECTORS + " VARCHAR, " +
                DescriptionTable.Cols.ACTORS + " VARCHAR, " +
                DescriptionTable.Cols.COUNTRY + " VARCHAR, " +
                DescriptionTable.Cols.YEAR + " VARCHAR, " +
                DescriptionTable.Cols.RATING + " VARCHAR " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_DESC_ID ON " + DescriptionTable.TABLE_NAME + " (" + DescriptionTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_DESC_DESC ON " + DescriptionTable.TABLE_NAME + " (" + DescriptionTable.Cols.DESCRIPTION + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + SchedulesTable.TABLE_NAME + "(" +
                SchedulesTable.Cols.ID + " INTEGER CONSTRAINT PK_SCH PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SchedulesTable.Cols.CHANNEL + " INTEGER CONSTRAINT FK_CHN_SCH REFERENCES " + ChannelsTable.TABLE_NAME + " (" + ChannelsTable.Cols.CHANNEL + ") ON DELETE CASCADE NOT NULL, " +
                SchedulesTable.Cols.CATEGORY + " INTEGER DEFAULT (0) CONSTRAINT FK_CAT_SCH REFERENCES " + CategoryTable.TABLE_NAME + " (" + CategoryTable.Cols.ID + ") ON DELETE CASCADE NOT NULL , " +
                SchedulesTable.Cols.STARTING + " DATETIME, " +
                SchedulesTable.Cols.ENDING + " DATETIME, " +
                SchedulesTable.Cols.TITLE + " VARCHAR " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_SCH_ID ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_CHANNEL ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.CHANNEL + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_CATEGORY ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.CATEGORY + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_START ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.STARTING + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_END ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.ENDING + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_TITLE ON " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.TITLE + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + SchedulesFavoritesTable.TABLE_NAME + "(" +
                SchedulesFavoritesTable.Cols.ID + " INTEGER CONSTRAINT PK_SCH_FAV PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                SchedulesFavoritesTable.Cols.SCHEDULE + " INTEGER CONSTRAINT FK_CHN_SCH REFERENCES " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.ID + ") ON DELETE CASCADE NOT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_SCH_FAV_ID ON " + SchedulesFavoritesTable.TABLE_NAME + " (" + SchedulesFavoritesTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_FAV_SCH ON " + SchedulesFavoritesTable.TABLE_NAME + " (" + SchedulesFavoritesTable.Cols.SCHEDULE + " ASC)");

        sqLiteDatabase.execSQL("CREATE TABLE " + ScheduleDescriptionTable.TABLE_NAME + "(" +
                ScheduleDescriptionTable.Cols.ID + " INTEGER CONSTRAINT PK_SCH_DESC PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                ScheduleDescriptionTable.Cols.SCHEDULE + " INTEGER CONSTRAINT FK_SCH_DESC_SCH REFERENCES " + SchedulesTable.TABLE_NAME + " (" + SchedulesTable.Cols.ID + ") ON DELETE CASCADE NOT NULL, " +
                ScheduleDescriptionTable.Cols.DESCRIPTION + " INTEGER CONSTRAINT FK_SCH_DESC_DESC REFERENCES " + DescriptionTable.TABLE_NAME + " (" + DescriptionTable.Cols.ID + ") ON DELETE CASCADE NOT NULL " +
                ")"
        );

        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX IDX_SCH_DESC_ID ON " + ScheduleDescriptionTable.TABLE_NAME + " (" + ScheduleDescriptionTable.Cols.ID + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_DESC_SCH ON " + ScheduleDescriptionTable.TABLE_NAME + " (" + ScheduleDescriptionTable.Cols.SCHEDULE + " ASC)");
        sqLiteDatabase.execSQL("CREATE INDEX IDX_SCH_DESC_DESC ON " + ScheduleDescriptionTable.TABLE_NAME + " (" + ScheduleDescriptionTable.Cols.DESCRIPTION + " ASC)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //
    }

    private void insertDataCategories(SQLiteDatabase database) {
        database.insert(CategoryTable.TABLE_NAME, null, getContentScheduleCategoryValues(new TVScheduleCategory(0, "Без категории", "")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentScheduleCategoryValues(new TVScheduleCategory(1, "Фильм", "х/ф,д/ф")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentScheduleCategoryValues(new TVScheduleCategory(2, "Сериал", "т/с,х/с,д/с")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentScheduleCategoryValues(new TVScheduleCategory(3, "Мультфильм", "м/с,м/ф,мульт")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentScheduleCategoryValues(new TVScheduleCategory(4, "Спорт", "спорт,футбол,хокей,uefa")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentScheduleCategoryValues(new TVScheduleCategory(5, "Новости", "новост,факты,тсн,новини,время,известия")));
        database.insert(CategoryTable.TABLE_NAME, null, getContentScheduleCategoryValues(new TVScheduleCategory(6, "Досуг", "истори,планет,разрушители,знаки,катастроф")));
    }

    public static String getSQLAllChannels(int index, int lang) {
        String sql_where = "";

        if (index == -1) {
            String sql_order = "ORDER BY ca." + ChannelsTable.Cols.CHANNEL;
            if (lang == 1) {
                sql_where = "WHERE ca." + ChannelsTable.Cols.LANG + "=" + DatabaseUtils.sqlEscapeString(RUS_LANG) + " ";
            }
            if (lang == 2) {
                sql_where = "WHERE ca." + ChannelsTable.Cols.LANG + "=" + DatabaseUtils.sqlEscapeString(UKR_LANG) + " ";
            }
            sql_where = sql_where + sql_order;
        } else {
            String channel = String.valueOf(index);
            sql_where = "WHERE ca." + ChannelsTable.Cols.CHANNEL + "=" + channel + " ";
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
                sql_where;

        return sql;
    }

    public static String getSQLFavoritesChannels() {
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

    public static String getSQLProgramsForChannelToDay(int id, String filter, Date date) {
        String sql_where = "";
        String sql_execdesc = "";

        if (id == -1) {
            String sql_order = " ORDER BY sch." + SchedulesTable.Cols.STARTING;
            sql_where = "WHERE (sch." + SchedulesTable.Cols.CHANNEL + "=" + filter + ")";
            if (date != null) {
                String sDate1 = DatabaseUtils.sqlEscapeString(getDateFormat(date, "yyyy-MM-dd"));
                String sDate2 = DatabaseUtils.sqlEscapeString(getDateFormat(addDays(date, 1), "yyyy-MM-dd"));
                sql_where = sql_where + " AND ((sch." + SchedulesTable.Cols.STARTING + ">=" + sDate1 + ") AND (sch." + SchedulesTable.Cols.STARTING + "<" + sDate2 + "))";
            }
            sql_execdesc = "0 AS " + SchedulesTable.Cols.EXDESC + " ";
            sql_where = sql_where + sql_order;
        } else {
            String idSchedule = String.valueOf(id);
            sql_execdesc = "CASE WHEN (SELECT sd." + ScheduleDescriptionTable.Cols.ID + " FROM " + ScheduleDescriptionTable.TABLE_NAME + " sd WHERE sch." + SchedulesTable.Cols.ID + "=sd." + ScheduleDescriptionTable.Cols.SCHEDULE + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.EXDESC + " ";
            sql_where = "WHERE sch." + SchedulesTable.Cols.ID + "=" + idSchedule;
        }

        String sql =
                "SELECT " +
                "sch." + SchedulesTable.Cols.ID + " AS " + SchedulesTable.Cols.ID + ", " +
                "sch." + SchedulesTable.Cols.CHANNEL + " AS " + SchedulesTable.Cols.CHANNEL + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + SchedulesTable.Cols.NAME + ", " +
                "CASE WHEN (SELECT cf." + ChannelsFavoritesTable.Cols.ID + " FROM " + ChannelsFavoritesTable.TABLE_NAME + " cf WHERE ca." + ChannelsTable.Cols.CHANNEL + "=cf." + ChannelsFavoritesTable.Cols.CHANNEL + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.FAVORITE_CHANNEL + ", " +
                "sch." + SchedulesTable.Cols.CATEGORY + " AS " + SchedulesTable.Cols.CATEGORY + ", " +
                "sch." + SchedulesTable.Cols.STARTING + " AS " + SchedulesTable.Cols.STARTING + ", " +
                "sch." + SchedulesTable.Cols.ENDING + " AS " + SchedulesTable.Cols.ENDING + ", " +
                "sch." + SchedulesTable.Cols.TITLE + " AS " + SchedulesTable.Cols.TITLE + ", " +
                "CASE WHEN (sch." + SchedulesTable.Cols.STARTING + "<=datetime('now','localtime')) AND (sch." + SchedulesTable.Cols.ENDING + ">=datetime('now','localtime')) THEN 1 WHEN sch." + SchedulesTable.Cols.STARTING + "<=datetime('now','localtime') THEN 0 else 2 END AS " + SchedulesTable.Cols.TIME_TYPE + ", " +
                "CASE WHEN (SELECT sf." + SchedulesFavoritesTable.Cols.ID + " FROM " + SchedulesFavoritesTable.TABLE_NAME + " sf WHERE sch." + SchedulesTable.Cols.ID + "=sf." + SchedulesFavoritesTable.Cols.SCHEDULE + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.FAVORITE + ", " +
                sql_execdesc +
                "FROM " +
                SchedulesTable.TABLE_NAME + " sch " +
                "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (sch." + SchedulesTable.Cols.CHANNEL + "=ca." + ChannelsTable.Cols.CHANNEL + ") " +
                sql_where;

        return sql;
    }

    public static String getSQLNowPrograms(String filter) {
        String sql_where = "";

        String sql_order = "ORDER BY ca." + ChannelsTable.Cols.NAME;
        int category = Integer.parseInt(filter);
        if (category > 0) {
            sql_where = "AND (sch." + SchedulesTable.Cols.CATEGORY + "=" + filter + ") " + sql_order;
        } else {
            sql_where = sql_order;
        }

        String sql =
                "SELECT " +
                "sch." + SchedulesTable.Cols.ID + " AS " + SchedulesTable.Cols.ID + ", " +
                "sch." + SchedulesTable.Cols.CHANNEL + " AS " + SchedulesTable.Cols.CHANNEL + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + SchedulesTable.Cols.NAME + ", " +
                "1 AS " + SchedulesTable.Cols.FAVORITE_CHANNEL + ", " +
                "sch." + SchedulesTable.Cols.CATEGORY + " AS " + SchedulesTable.Cols.CATEGORY + ", " +
                "sch." + SchedulesTable.Cols.STARTING + " AS " + SchedulesTable.Cols.STARTING + ", " +
                "sch." + SchedulesTable.Cols.ENDING + " AS " + SchedulesTable.Cols.ENDING + ", " +
                "sch." + SchedulesTable.Cols.TITLE + " AS " + SchedulesTable.Cols.TITLE + ", " +
                "1 AS " + SchedulesTable.Cols.TIME_TYPE + ", " +
                "CASE WHEN (SELECT sf." + SchedulesFavoritesTable.Cols.ID + " FROM " + SchedulesFavoritesTable.TABLE_NAME + " sf WHERE sch." + SchedulesTable.Cols.ID + "=sf." + SchedulesFavoritesTable.Cols.SCHEDULE + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.FAVORITE + ", " +
                "0 AS " + SchedulesTable.Cols.EXDESC + " " +
                "FROM " +
                SchedulesTable.TABLE_NAME + " sch " +
                "JOIN " + ChannelsFavoritesTable.TABLE_NAME + " cf ON (sch." + SchedulesTable.Cols.CHANNEL + "=cf." + ChannelsFavoritesTable.Cols.CHANNEL + ") " +
                "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (cf." + ChannelsFavoritesTable.Cols.CHANNEL + "=ca." + ChannelsTable.Cols.CHANNEL + ") " +
                "WHERE (sch." + SchedulesTable.Cols.STARTING + "<=datetime('now','localtime')) AND (sch." + SchedulesTable.Cols.ENDING + ">datetime('now','localtime')) " +
                sql_where;

        return sql;
    }

    public static String getSQLFavoritesPrograms() {
        String sql_where = "";
        String sql_order = "ORDER BY ca." + ChannelsTable.Cols.NAME + ", sch." + SchedulesTable.Cols.STARTING;
        sql_where = sql_order;

        String sql =
                "SELECT " +
                "sch." + SchedulesTable.Cols.ID + " AS " + SchedulesTable.Cols.ID + ", " +
                "sch." + SchedulesTable.Cols.CHANNEL + " AS " + SchedulesTable.Cols.CHANNEL + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + SchedulesTable.Cols.NAME + ", " +
                "CASE WHEN (SELECT cf." + ChannelsFavoritesTable.Cols.ID + " FROM " + ChannelsFavoritesTable.TABLE_NAME + " cf WHERE ca." + ChannelsTable.Cols.CHANNEL + "=cf." + ChannelsFavoritesTable.Cols.CHANNEL + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.FAVORITE_CHANNEL + ", " +
                "sch." + SchedulesTable.Cols.CATEGORY + " AS " + SchedulesTable.Cols.CATEGORY + ", " +
                "sch." + SchedulesTable.Cols.STARTING + " AS " + SchedulesTable.Cols.STARTING + ", " +
                "sch." + SchedulesTable.Cols.ENDING + " AS " + SchedulesTable.Cols.ENDING + ", " +
                "sch." + SchedulesTable.Cols.TITLE + " AS " + SchedulesTable.Cols.TITLE + ", " +
                "CASE WHEN (sch." + SchedulesTable.Cols.STARTING + "<=datetime('now','localtime')) AND (sch." + SchedulesTable.Cols.ENDING + ">=datetime('now','localtime')) THEN 1 WHEN sch." + SchedulesTable.Cols.STARTING + "<=datetime('now','localtime') THEN 0 else 2 END AS " + SchedulesTable.Cols.TIME_TYPE + ", " +
                "1 AS " + SchedulesTable.Cols.FAVORITE + ", " +
                "0 AS " + SchedulesTable.Cols.EXDESC + " " +
                "FROM " +
                SchedulesTable.TABLE_NAME + " sch " +
                "JOIN " + SchedulesFavoritesTable.TABLE_NAME + " sf ON (sch." + SchedulesTable.Cols.ID + "=sf." + SchedulesFavoritesTable.Cols.SCHEDULE + ") " +
                "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (sch." + SchedulesTable.Cols.CHANNEL + "=ca." + ChannelsTable.Cols.CHANNEL + ") " +
                sql_where;

        return sql;
    }

    public static String getSQLSearchPrograms(String filter) {
        String sql_where = "";
        String sql_order = "ORDER BY ca." + ChannelsTable.Cols.NAME + ", sch." + SchedulesTable.Cols.STARTING;
        if (!filter.equals("")) {
            sql_where = "WHERE sch." + SchedulesTable.Cols.TITLE + " LIKE " + DatabaseUtils.sqlEscapeString("%" + filter + "%") + " " + sql_order;
        } else {
            sql_where = "WHERE sch." + SchedulesTable.Cols.ID + "=-1 ";
        }

        String sql =
                "SELECT " +
                "sch." + SchedulesTable.Cols.ID + " AS " + SchedulesTable.Cols.ID + ", " +
                "sch." + SchedulesTable.Cols.CHANNEL + " AS " + SchedulesTable.Cols.CHANNEL + ", " +
                "ca." + ChannelsTable.Cols.NAME + " AS " + SchedulesTable.Cols.NAME + ", " +
                "CASE WHEN (SELECT cf." + ChannelsFavoritesTable.Cols.ID + " FROM " + ChannelsFavoritesTable.TABLE_NAME + " cf WHERE ca." + ChannelsTable.Cols.CHANNEL + "=cf." + ChannelsFavoritesTable.Cols.CHANNEL + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.FAVORITE_CHANNEL + ", " +
                "sch." + SchedulesTable.Cols.CATEGORY + " AS " + SchedulesTable.Cols.CATEGORY + ", " +
                "sch." + SchedulesTable.Cols.STARTING + " AS " + SchedulesTable.Cols.STARTING + ", " +
                "sch." + SchedulesTable.Cols.ENDING + " AS " + SchedulesTable.Cols.ENDING + ", " +
                "sch." + SchedulesTable.Cols.TITLE + " AS " + SchedulesTable.Cols.TITLE + ", " +
                "CASE WHEN (sch." + SchedulesTable.Cols.STARTING + "<=datetime('now','localtime')) AND (sch." + SchedulesTable.Cols.ENDING + ">=datetime('now','localtime')) THEN 1 WHEN sch." + SchedulesTable.Cols.STARTING + "<=datetime('now','localtime') THEN 0 else 2 END AS " + SchedulesTable.Cols.TIME_TYPE + ", " +
                "CASE WHEN (SELECT sf." + SchedulesFavoritesTable.Cols.ID + " FROM " + SchedulesFavoritesTable.TABLE_NAME + " sf WHERE sch." + SchedulesTable.Cols.ID + "=sf." + SchedulesFavoritesTable.Cols.SCHEDULE + ") IS NOT NULL THEN 1 ELSE 0 END AS " + SchedulesTable.Cols.FAVORITE + ", " +
                "0 AS " + SchedulesTable.Cols.EXDESC + " " +
                "FROM " +
                SchedulesTable.TABLE_NAME + " sch " +
                "JOIN " + ChannelsTable.TABLE_NAME + " ca ON (sch." + SchedulesTable.Cols.CHANNEL + "=ca." + ChannelsTable.Cols.CHANNEL + ") " +
                sql_where;

        return sql;
    }

    public static String getSQLDescription(int scheduleId) {
        String schedule = String.valueOf(scheduleId);

        String sql =
                "SELECT " +
                "sd." + ScheduleDescriptionTable.Cols.SCHEDULE + " AS " + ScheduleDescriptionTable.Cols.SCHEDULE + ", " +
                "desc." + DescriptionTable.Cols.ID + " AS " + DescriptionTable.Cols.ID + ", " +
                "desc." + DescriptionTable.Cols.DESCRIPTION + " AS " + DescriptionTable.Cols.DESCRIPTION + ", " +
                "desc." + DescriptionTable.Cols.IMAGE + " AS " + DescriptionTable.Cols.IMAGE + ", " +
                "desc." + DescriptionTable.Cols.GENRES + " AS " + DescriptionTable.Cols.GENRES + ", " +
                "desc." + DescriptionTable.Cols.ACTORS + " AS " + DescriptionTable.Cols.ACTORS + ", " +
                "desc." + DescriptionTable.Cols.DIRECTORS + " AS " + DescriptionTable.Cols.DIRECTORS + ", " +
                "desc." + DescriptionTable.Cols.COUNTRY + " AS " + DescriptionTable.Cols.COUNTRY + ", " +
                "desc." + DescriptionTable.Cols.YEAR + " AS " + DescriptionTable.Cols.YEAR + ", " +
                "desc." + DescriptionTable.Cols.RATING + " AS " + DescriptionTable.Cols.RATING + " " +
                "FROM " + ScheduleDescriptionTable.TABLE_NAME + " sd " +
                "JOIN " + DescriptionTable.TABLE_NAME + " desc ON (sd." + ScheduleDescriptionTable.Cols.DESCRIPTION + "=desc." + DescriptionTable.Cols.ID + ") " +
                "WHERE sd." + ScheduleDescriptionTable.Cols.SCHEDULE + "=" + schedule;

        return sql;
    }
}
