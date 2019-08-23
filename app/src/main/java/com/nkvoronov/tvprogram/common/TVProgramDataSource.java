package com.nkvoronov.tvprogram.common;

import java.io.File;
import java.util.Date;
import java.util.List;
import android.util.Log;
import java.util.ArrayList;
import android.content.Context;
import android.database.Cursor;
import android.widget.ImageView;
import java.text.ParseException;
import com.nkvoronov.tvprogram.R;
import java.text.SimpleDateFormat;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import com.nkvoronov.tvprogram.tvprogram.TVProgram;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvprogram.TVProgramsList;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.*;
import com.nkvoronov.tvprogram.database.TVProgramBaseHelper;
import com.nkvoronov.tvprogram.tvprogram.TVProgramCategoriesList;
import static com.nkvoronov.tvprogram.common.StringUtils.addQuotes;

public class TVProgramDataSource {
    public static final String RUS_LANG = "rus";
    public static final String UKR_LANG = "ukr";
    public static final String ALL_LANG = "all";
    public static final String TAG = "TVPROGRAM";

    private Context mContext;
    private AppConfig mAppConfig;
    private SQLiteDatabase mDatabase;
    private static TVProgramDataSource sTVProgramLab;

    public TVProgramDataSource(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new TVProgramBaseHelper(mContext).getWritableDatabase();
        mAppConfig = new AppConfig(this);
    }

    public static TVProgramDataSource get(Context context) {
        if (sTVProgramLab == null) {
            sTVProgramLab = new TVProgramDataSource(context);
        }
        return sTVProgramLab;
    }

    public int getCoutDays() {
        return mAppConfig.getCoutDays();
    }

    public void setCoutDays(int coutDays) {
        mAppConfig.setCoutDays(coutDays);
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
            mDatabase.insert(ChannelsFavoritesTable.TABLE_NAME, null, getContentChannelsFavValues(channel));
        }
    }

    private ContentValues getContentChannelsFavValues(TVChannel channel) {
        ContentValues values = new ContentValues();
        values.put(ChannelsFavoritesTable.Cols.CHANNEL, channel.getIndex());
        return values;
    }

    public void programChangeFavorites(TVProgram program) {
        if (program.isFavorites()) {
            mDatabase.delete(SchedulesFavoritesTable.TABLE_NAME, SchedulesFavoritesTable.Cols.SCHEDULE + " = ?", new String[]{String.valueOf(program.getId())});
        } else {
            mDatabase.insert(SchedulesFavoritesTable.TABLE_NAME, null, getContentSchedulesFavValues(program));
        }
    }

    private ContentValues getContentSchedulesFavValues(TVProgram program) {
        ContentValues values = new ContentValues();
        values.put(SchedulesFavoritesTable.Cols.SCHEDULE, program.getId());
        return values;
    }

    public TVChannelsList getChannels(boolean isFavorites, int filter) {
        TVChannelsList channels = new TVChannelsList(this);
        channels.loadFromDB(isFavorites, filter);
        return channels;
    };

    public TVChannel getChannel(int index) {
        TVChannelsList channelList = getChannels(false, 0);
        return channelList.getForIndex(index);
    }

    public TVProgramsList getPrograms(int type, String filter, Date date) {
        TVProgramsList programs = new TVProgramsList(this);
        programs.loadFromDB(type, filter, date);
        return programs;
    };

    public TVProgram getProgram(int index, int programID) {
        TVProgramsList programs = getPrograms(0, String.valueOf(index), null);
        return programs.getForId(programID);
    }

    public TVProgramCategoriesList getCategories() {
        TVProgramCategoriesList categories = new TVProgramCategoriesList(this);
        categories.loadFromDB();
        return categories;
    }

    public List<String> getCategoriesList() {
        List<String> list = new ArrayList<>();
        list.add(mContext.getString(R.string.category_all));
        TVProgramCategoriesList categories = getCategories();
        for (int i = 0; i<categories.size(); i++) {
            list.add(categories.get(i).getName());
        }
        return list;
    }

    public File getChannelIconFile(int index) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, "IMG_" + Integer.toString(index) + ".gif");
    }

    public boolean checkFavoritesChannel() {
        return true;
    }

    public boolean checkUpdateProgram(int channel) {
        String index = new Integer(channel).toString();
        String now_date = addQuotes(getDateFormat(new Date(), "yyyy-MM-dd"), "'");
        boolean res = true;
        Cursor cursor = mDatabase.query(SchedulesTable.TABLE_NAME,
                null,
                "(" + SchedulesTable.Cols.CHANNEL + "=" + index + ") AND (" + SchedulesTable.Cols.STARTING + ">=" + now_date + ")",
                null,
                null,
                null,
                null);
        try {
           if (cursor.getCount() != 0) {
               res = false;
           }
        } finally {
            cursor.close();
        }

        return res;
    }

    public long[] getProgramInfo(int channel) {
        long[] info = {0, 0, 0};
        String sChannel = String.valueOf(channel);
        String sql =
                "SELECT " +
                "min(" + SchedulesTable.Cols.STARTING + ") as min, " +
                "max(" + SchedulesTable.Cols.STARTING + ") as max " +
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
                id_res = R.drawable.category_hobbies;
                break;
            default:
                break;
        }
        image.setImageResource(id_res);
    }
}
