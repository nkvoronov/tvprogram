package com.nkvoronov.tvprogram.tasks;

import android.os.AsyncTask;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import org.jsoup.select.Elements;
import java.util.Date;
import static com.nkvoronov.tvprogram.common.DateUtils.getFormatDate;
import static com.nkvoronov.tvprogram.common.HttpContent.HOST;

public class UpdateProgramsTask extends AsyncTask<Void,Integer,Void> {
    public static final String STR_SCHEDULECHANNEL = "schedule_channel_%d_day_%s.html";
    public static final String STR_ELMDOCSELECT = "div[class~=(?:pasttime|onair|time)]";
    public static final String STR_ELMDOCTITLE = "div[class~=(?:pastprname2|prname2)]";
    public static final String STR_ELMDOCDESC = "div[class~=(?:pastdesc|prdesc)]";

    private OnTaskListeners mListeners;
    private TVProgramDataSource mDataSource;

    public interface OnTaskListeners {
        public void onStart();
        public void onUpdate(int progress);
        public void onStop();
    }

    public void setListeners(OnTaskListeners listeners) {
        mListeners = listeners;
    }

    public void setDataSource(TVProgramDataSource dataSource) {
        mDataSource = dataSource;
    }

    @Override
    protected void onPreExecute() {
        mListeners.onStart();
    }

    public void getContentDay(TVChannel channel, Date date) {
        String vdirection = String.format(STR_SCHEDULECHANNEL, channel.getIndex(), getFormatDate(date, "yyyy-MM-dd"));
        org.jsoup.nodes.Document doc = new HttpContent(HOST + vdirection).getDocument();
        Elements items = doc.select(STR_ELMDOCSELECT);
        for (org.jsoup.nodes.Element item : items){
            //
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        mListeners.onUpdate(values[0]);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListeners.onStop();
    }
}
