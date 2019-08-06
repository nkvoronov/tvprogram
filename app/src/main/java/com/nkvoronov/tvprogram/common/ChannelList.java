package com.nkvoronov.tvprogram.common;

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
import java.util.List;
import static com.nkvoronov.tvprogram.common.HttpContent.CHANNELS_PRE;
import static com.nkvoronov.tvprogram.common.HttpContent.ICONS_PRE;

public class ChannelList implements Runnable{
    public static final String CHANNELS_SELECT = "option[value^=channel_]";
    public static final String TAG = "CHANNEL_LIST";

    private String mLang;
    private Boolean mIndexSort;
    private List<Channel> mData;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public ChannelList(Context context, String lang, Boolean indexSort) {
        mContext = context.getApplicationContext();
        mLang = lang;
        mIndexSort = indexSort;
        mDatabase = TVProgramLab.get(context).getDatabase();
        mData = new ArrayList<>();
        Log.d(TAG, "ChannelList " + mData.size());
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

    public void loadFromNet() {
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
            Log.d(TAG, channel_name);
            flag = channel_name.endsWith("(на укр.)");
            if ((!flag && mLang.equals("ru"))||(flag && mLang.equals("ua"))) {
                channel_link = element.attr("value");
                channel_index = channel_link.split("_")[1];
                channel_icon = ICONS_PRE + channel_index + ".gif";
                Channel channel = new Channel(0, Integer.parseInt(channel_index), channel_name, channel_name, channel_icon, 120);
                channel.setIsFav(false);
                channel.setIsUpd(false);
                mData.add(channel);
            }
        }
        if (mIndexSort) {
            Collections.sort(mData, Channel.channelIndexComparator);
        } else {
            Collections.sort(mData, Channel.channelNameComparator);
        }

    }

    public void loadFromDB() {
        mData.clear();
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

    public void saveChannelToDB(Channel channel, int index) {
        //
    }

    public void saveChannelIcon(Channel channel, int index) {
        //
    }

    @Override
    public void run() {
        //
    }
}
