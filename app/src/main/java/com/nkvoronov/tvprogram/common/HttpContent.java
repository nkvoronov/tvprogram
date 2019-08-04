package com.nkvoronov.tvprogram.common;

import android.os.StrictMode;
import android.util.Log;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class HttpContent {
    public static final String HOST = "http://www.vsetv.com/";
    public static final String ICONS_PRE = HOST + "pic/channel_logos/";
    public static final String CHANNELS_PRE = HOST + "channels.html";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; rv:5.0) Gecko/20100101 Firefox/5.0";
    public static final String TAG = "HTTPCONNECT";

    private String mUrl;
    private Document mDocument;

    public HttpContent(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mUrl = url;
        Connection connection = Jsoup.connect(url);
        connection.userAgent(USER_AGENT);
        try {
            mDocument = connection.get();
            Log.d(TAG, "HttpContent " + url);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            e.fillInStackTrace();
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Document getDocument() {
        Log.d(TAG, "getDocument");
        return mDocument;
    }

}
