package com.nkvoronov.tvprogram.common;

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;
import android.util.Log;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.DataInputStream;
import android.content.Context;
import android.database.Cursor;
import java.io.FileOutputStream;
import android.widget.ImageView;
import java.text.ParseException;
import com.nkvoronov.tvprogram.R;
import java.text.SimpleDateFormat;
import java.io.FileNotFoundException;
import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvschedule.TVSchedule;
import com.nkvoronov.tvprogram.database.MainDbSchema.*;
import com.nkvoronov.tvprogram.database.MainBaseHelper;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import com.nkvoronov.tvprogram.tvschedule.TVSchedulesList;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleCategoriesList;

public class MainDataSource {
    public static final String RUS_LANG = "rus";
    public static final String UKR_LANG = "ukr";
    public static final String ALL_LANG = "all";
    public static final String TAG = "TVPROGRAM";

    private Context mContext;
    private AppConfig mAppConfig;
    private SQLiteDatabase mDatabase;
    private static MainDataSource sTVProgramLab;

    public MainDataSource(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new MainBaseHelper(mContext).getWritableDatabase();
        mAppConfig = new AppConfig(this);
    }

    public static MainDataSource get(Context context) {
        if (sTVProgramLab == null) {
            sTVProgramLab = new MainDataSource(context);
        }
        return sTVProgramLab;
    }

    public int getCoutDays() {
        return mAppConfig.getCoutDays();
    }

    public void setCoutDays(int coutDays) {
        mAppConfig.setCoutDays(coutDays);
    }

    public boolean isFullDesc() {
        return mAppConfig.isFullDesc();
    }

    public void setFullDesc(boolean fullDesc) {
        mAppConfig.setFullDesc(fullDesc);
    }

    public Context getContext() {
        return mContext;
    }

    public SQLiteDatabase getDatabase() {
        return mDatabase;
    }

    public void channelChangeFavorites(TVChannel channel) {
        if (channel.isFavorites()) {
            mDatabase.delete(ChannelsFavoritesTable.TABLE_NAME, ChannelsFavoritesTable.Cols.CHANNEL + " = ?", new String[]{String.valueOf(channel.getIndex())});
        } else {
            mDatabase.insert(ChannelsFavoritesTable.TABLE_NAME, null, getContentChannelsFavoritesValues(channel));
        }
    }

    private ContentValues getContentChannelsFavoritesValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        values.put(ChannelsFavoritesTable.Cols.CHANNEL, channel.getIndex());
        return values;
    }

    public void scheduleChangeFavorites(TVSchedule schedule) {
        if (schedule.isFavorites()) {
            mDatabase.delete(SchedulesFavoritesTable.TABLE_NAME, SchedulesFavoritesTable.Cols.SCHEDULE + " = ?", new String[]{String.valueOf(schedule.getId())});
        } else {
            mDatabase.insert(SchedulesFavoritesTable.TABLE_NAME, null, getContentSchedulesFavoritesValues(schedule));
        }
    }

    private ContentValues getContentSchedulesFavoritesValues(TVSchedule program) {
        ContentValues values = new ContentValues();
        values.put(SchedulesFavoritesTable.Cols.SCHEDULE, program.getId());
        return values;
    }

    public TVChannelsList getChannels(boolean isFavorites, int lang) {
        TVChannelsList channels = new TVChannelsList(this);
        channels.loadFromDB(isFavorites, lang);
        return channels;
    };

    public TVChannel getChannel(int index) {
        TVChannelsList channels = new TVChannelsList(this);
        return channels.getForIndex(index);
    }

    public TVSchedulesList getSchedules(int type, String filter, Date date) {
        TVSchedulesList schedules = new TVSchedulesList(this);
        schedules.loadFromDB(type, filter, date);
        return schedules;
    };

    public TVSchedule getSchedule(int scheduleID) {
        TVSchedulesList schedules = new TVSchedulesList(this);
        TVSchedule schedule = schedules.getForId(scheduleID);
        schedule.setDescriptionFromDB();
        return schedule;
    }

    public TVScheduleCategoriesList getCategories() {
        TVScheduleCategoriesList categories = new TVScheduleCategoriesList(this);
        categories.loadFromDB();
        return categories;
    }

    public List<String> getCategoriesList() {
        List<String> list = new ArrayList<>();
        list.add(mContext.getString(R.string.cat_all));
        TVScheduleCategoriesList categories = getCategories();
        for (int i = 0; i<categories.size(); i++) {
            list.add(categories.get(i).getName());
        }
        return list;
    }

    public File getChannelIconFile(int index) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, "ICON_" + Integer.toString(index) + ".gif");
    }

    public File getDescriptionImageFile(String type, int catalog) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, "IMAGE_" + type + "_" + Integer.toString(catalog) + ".jpg");
    }

    public void saveFileFromNet(File file, String string_url) {
        try {
            if (!file.exists()) {
                URL url = new URL(string_url);
                InputStream inputStream = url.openStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = dataInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, length);
                }
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, e.getMessage());
            e.fillInStackTrace();
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.fillInStackTrace();
        }
    }

    public void clearDB() {
        mDatabase.delete(DescriptionTable.TABLE_NAME, null, null);
        mDatabase.delete(ScheduleDescriptionTable.TABLE_NAME, null, null);
        mDatabase.delete(SchedulesFavoritesTable.TABLE_NAME, null, null);
        mDatabase.delete(SchedulesTable.TABLE_NAME, null, null);
        mDatabase.delete(ChannelsFavoritesTable.TABLE_NAME, null, null);
        mDatabase.delete(ChannelsTable.TABLE_NAME, null, null);
    }

    public boolean checkFavoritesChannel() {
        boolean isExFav = false;

        String sql =
                "SELECT " +
                "count(" + ChannelsFavoritesTable.Cols.ID + ") AS cnt " +
                "FROM " +
                ChannelsFavoritesTable.TABLE_NAME;

        Cursor cursor = mDatabase.rawQuery(sql, null, null);

        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                int cnt = cursor.getInt(cursor.getColumnIndex("cnt"));
                isExFav = cnt > 0;
            }
        } finally {
            cursor.close();
        }
        return isExFav;
    }

    public boolean checkUpdateSchedule(int channel) {
        String index = new Integer(channel).toString();
        String now_date = DatabaseUtils.sqlEscapeString(getDateFormat(new Date(), "yyyy-MM-dd"));
        boolean isUpdate = true;

        Cursor cursor = mDatabase.query(SchedulesTable.TABLE_NAME,
                null,
                "(" + SchedulesTable.Cols.CHANNEL + "=?) AND (" + SchedulesTable.Cols.STARTING + ">=?)",
                new String[] {index, now_date},
                null,
                null,
                null);

        try {
           if (cursor.getCount() != 0) {
               isUpdate = false;
           }
        } finally {
            cursor.close();
        }
        return isUpdate;
    }

    public long[] getScheduleInfo(int channel) {
        long[] info = {0, 0, 0};
        String sChannel = String.valueOf(channel);

        String sql =
                "SELECT " +
                "min(" + SchedulesTable.Cols.STARTING + ") AS min, " +
                "max(" + SchedulesTable.Cols.STARTING + ") AS max " +
                "FROM " +
                SchedulesTable.TABLE_NAME + " " +
                "WHERE " + SchedulesTable.Cols.CHANNEL + "=" + sChannel;

        Cursor cursor = mDatabase.rawQuery(sql, null, null);

        try {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                String min = cursor.getString(cursor.getColumnIndex("min"));
                String max = cursor.getString(cursor.getColumnIndex("max"));
                String now = getDateFormat(new Date(), "yyyy-MM-dd");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date minDate = null;
                Date maxDate = null;
                Date nowDate = null;

                try {
                    minDate = simpleDateFormat.parse(min);
                    maxDate = simpleDateFormat.parse(max);
                    nowDate = simpleDateFormat.parse(now);
                } catch (ParseException e) {
                    e.fillInStackTrace();
                    Log.d(TAG, e.getMessage());
                }

                info[0] = minDate.getTime();
                info[1] = (maxDate.getTime() - minDate.getTime()) / 86400000;
                info[2] = (nowDate.getTime() - minDate.getTime()) / 86400000;
            }
        } finally {
            cursor.close();
        }
        return info;
    }

    public void setCategoryDrawable(ImageView image, int category) {
        int id_res = 0;
        switch (category) {
            case 1:
                id_res = R.drawable.category_movie;
                break;
            case 2:
                id_res = R.drawable.category_tvshow;
                break;
            case 3:
                id_res = R.drawable.category_cartoons;
                break;
            case 4:
                id_res = R.drawable.category_sports;
                break;
            case 5:
                id_res = R.drawable.category_news;
                break;
            case 6:
                id_res = R.drawable.category_cognitive;
                break;
            case 7:
                id_res = R.drawable.category_show;
                break;
            default:
                break;
        }
        image.setImageResource(id_res);
    }
}
