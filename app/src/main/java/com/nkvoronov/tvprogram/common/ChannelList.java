package com.nkvoronov.tvprogram.common;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.nkvoronov.tvprogram.database.ChannelsAllCursorWrapper;
import com.nkvoronov.tvprogram.database.TvChannelsCursorWrapper;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsAllTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsFavTable;
import com.nkvoronov.tvprogram.database.TVProgramDbSchema.ChannelsTvTable;
import static com.nkvoronov.tvprogram.common.HttpContent.CHANNELS_PRE;
import static com.nkvoronov.tvprogram.common.HttpContent.ICONS_PRE;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;


public class ChannelList implements Runnable{
    public static final String CHANNELS_SELECT = "option[value^=channel_]";

    private String mLang;
    private Boolean mIndexSort;
    private List<Channel> mData;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public ChannelList(Context context, String lang, Boolean indexSort) {
        mContext = context.getApplicationContext();
        mLang = lang;
        mIndexSort = indexSort;
        mDatabase = TVProgramDataSource.get(context).getDatabase();
        mData = new ArrayList<>();
        //Log.d(TAG, "ChannelList " + mData.size());
    }

    public File getIconFile(Channel channel) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, channel.getIconFilename());
    }

    public String getLang() {
        return mLang;
    }

    public void setLang(String lang) {
        mLang = lang;
    }

    public Boolean getIndexSort() {
        return mIndexSort;
    }

    public void setIndexSort(Boolean indexSort) {
        mIndexSort = indexSort;
    }

    public List<Channel> getData() {
        return mData;
    }

    public Context getContext() {
        return mContext;
    }

    public Channel getChannel(int i) {
        return mData.get(i);
    }

    public int getSize() {
        return mData.size();
    }

    public void loadFromNetAndUpdate(boolean isUpdate) {
        String channel_index;
        String channel_name;
        String channel_link;
        String channel_icon;
        Boolean flag = false;

        mData.clear();
        org.jsoup.nodes.Document document = new HttpContent(CHANNELS_PRE).getDocument();
        org.jsoup.select.Elements elements = document.select(CHANNELS_SELECT);
        for (org.jsoup.nodes.Element element : elements){
            channel_name = element.text();
            flag = channel_name.endsWith("(на укр.)");
            if ((!flag && mLang.equals("rus"))||(flag && mLang.equals("ukr"))) {
                channel_link = element.attr("value");
                channel_index = channel_link.split("_")[1];
                channel_icon = ICONS_PRE + channel_index + ".gif";
                Channel channel = new Channel(0, Integer.parseInt(channel_index), channel_name, channel_name, channel_icon, 120);
                channel.setIsFav(false);
                channel.setIsUpd(false);
                mData.add(channel);
                Log.d(TAG, channel.toString());
                if (isUpdate) {
                    saveChannelToDB(channel);
                    saveChannelIcon(channel);
                }
            }
        }
        if (mIndexSort) {
            Collections.sort(mData, Channel.channelIndexComparator);
        } else {
            Collections.sort(mData, Channel.channelNameComparator);
        }

    }

    public void loadFromDB(boolean isFav) {
        String selection = null;
        String[] selectionArg = null;
        String orderBy = ChannelsTvTable.Cols.CHANNEL_INDEX;
        mData.clear();

        if (isFav) {
            selection = ChannelsTvTable.Cols.FAVORITE + " = ?";
            selectionArg = new String[]{ "1" };
            orderBy = ChannelsTvTable.Cols.USER_NAME;
        }

        TvChannelsCursorWrapper cursor = new TvChannelsCursorWrapper(mDatabase.query(
                ChannelsTvTable.TABLE_NAME,
                null,
                selection,
                selectionArg,
                null,
                null,
                orderBy
        ));

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                mData.add(cursor.getChannel());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
    }

    public void loadFromFile(String fileName) {
        mData.clear();
        File file = new File(fileName);
        try {
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                    e.fillInStackTrace();
                }
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] aline = line.split(Channel.SEPARATOR);
                Channel channel = new Channel(0, Integer.parseInt(aline[0]), aline[1], aline[2], aline[3], Integer.parseInt(aline[4]));
                channel.setIsFav(false);
                channel.setIsUpd(false);
                mData.add(channel);
            }
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.fillInStackTrace();
        }
    }

    public void saveToFile(String fileName) {
        File file = new File(fileName);
        try {
            if(!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                    e.fillInStackTrace();
                }
            }
            PrintWriter printWriter = new PrintWriter(file.getAbsoluteFile());
            for (Channel channel : mData) {
                printWriter.println(channel.toString());
            }
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (Channel channel : mData) {
            result += channel.toString() + "\n";
        }
        return result;
    }

    public void getXML(Document document, Element element) {
        for (Channel channel : mData) {
            channel.getXML(document, element);
        }
    }

    public void saveChannelToDB(Channel channel) {
        int typeUpdate = 0;
        String oldName = "";
        ChannelsAllCursorWrapper cursor = new ChannelsAllCursorWrapper(mDatabase.query(
                ChannelsAllTable.TABLE_NAME,
                null,
                ChannelsAllTable.Cols.CHANNEL_INDEX + " = ?",
                new String[]{String.valueOf(channel.getIndex())},
                null,
                null,
                null
        ));
        Log.d(TAG, "saveChannelToDB : " + String.valueOf(channel.getIndex()));
        try {
            if (cursor.getCount() != 0) {
                typeUpdate = 0;
                cursor.moveToFirst();
                oldName = cursor.getName();
                if (!channel.getOName().equals(oldName)) {
                    typeUpdate = 1;
                }
                Log.d(TAG, "saveChannelToDB update");
            } else {
                typeUpdate = 2;
                Log.d(TAG, "saveChannelToDB insert");
            }
        } finally {
            cursor.close();
        }
        if (typeUpdate == 1) {
            updateChannel(channel);
        }
        if (typeUpdate == 2) {
            insertChannel(channel);
        }
    }

    private void insertChannel(Channel channel) {
        mDatabase.insert(ChannelsAllTable.TABLE_NAME, null, getContentChannelsAllValues(channel));
    }

    private void updateChannel(Channel channel) {
        mDatabase.update(ChannelsAllTable.TABLE_NAME, getContentChannelsAllValues(channel), ChannelsAllTable.Cols.CHANNEL_INDEX + " = ?", new String[]{String.valueOf(channel.getIndex())});
    }

    public void addFavorites(Channel channel) {
        //
    }

    public void delFavorites(Channel channel) {
        //
    }

    public void saveChannelIcon(Channel channel) {
        //
    }

    @Override
    public void run() {
        //
    }

    private ContentValues getContentChannelsAllValues(Channel channel) {
        ContentValues values = new ContentValues();
        Date date = new Date();
        values.put(ChannelsAllTable.Cols.CHANNEL_INDEX, channel.getIndex());
        values.put(ChannelsAllTable.Cols.NAME, channel.getOName());
        values.put(ChannelsAllTable.Cols.ICON, channel.getIcon());
        values.put(ChannelsAllTable.Cols.UPD_CHANNEL, date.getTime());
        return values;
    }

    private ContentValues getContentChannelsFavValues(Channel channel) {
        ContentValues values = new ContentValues();
        values.put(ChannelsFavTable.Cols.CHANNEL_INDEX, channel.getIndex());
        values.put(ChannelsFavTable.Cols.NAME, channel.getUName());
        values.put(ChannelsFavTable.Cols.CORRECTION, channel.getCorrection());
        return values;
    }
}
