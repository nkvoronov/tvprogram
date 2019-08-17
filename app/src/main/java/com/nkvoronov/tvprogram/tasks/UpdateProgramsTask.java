package com.nkvoronov.tvprogram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.tvprogram.TVProgram;
import com.nkvoronov.tvprogram.tvprogram.TVProgramsList;
import org.jsoup.select.Elements;
import java.util.Date;
import java.util.Objects;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import static com.nkvoronov.tvprogram.common.HttpContent.HOST;

public class UpdateProgramsTask extends AsyncTask<Integer,String,Void> {
    public static final String STR_SCHEDULECHANNEL = "schedule_channel_%d_day_%s.html";
    public static final String STR_ELMDOCSELECT = "div[class~=(?:pasttime|onair|time)]";
    public static final String STR_ELMDOCTITLE = "div[class~=(?:pastprname2|prname2)]";
    public static final String STR_ELMDOCDESC = "div[class~=(?:pastdesc|prdesc)]";
    public static final int DEF_CORRECTION = 120;

    private OnTaskListeners mListeners;
    private TVProgramDataSource mDataSource;
    private TVChannelsList mChannels;
    private TVProgramsList mPrograms;

    private String[] progress;
    private int index;
    private int counter;
    private int total;

    private boolean mFullDesc;

    public UpdateProgramsTask(TVProgramDataSource dataSource) {
        mDataSource = dataSource;
        mChannels = null;
        index = 0;
        mFullDesc = false;
    }

    public interface OnTaskListeners {
        public void onStart();
        public void onUpdate(String[] progress);
        public void onStop();
    }

    public void setListeners(OnTaskListeners listeners) {
        mListeners = listeners;
    }

    public void setDataSource(TVProgramDataSource dataSource) {
        mDataSource = dataSource;
    }

    public void setFullDesc(boolean fullDesc) {
        mFullDesc = fullDesc;
    }

    @Override
    protected void onPreExecute() {
        mListeners.onStart();
    }

    public void getContentForChannel(int channel, Date date) {
        Date lastdate = addDays(date, mDataSource.getCoutDays());
        Log.d(TAG, "LAST " + getFormatDate(lastdate, "yyyy-MM-dd"));
        while (date.compareTo(lastdate) != 0) {
            progress[1] = getFormatDate(date, "yyyy-MM-dd");
            getContentForDay(channel, date);
            counter = (int) (((index+1) / (float) total) * 100);
            progress[2] = String.valueOf(counter);
            publishProgress(progress);
            if(isCancelled()){
                break;
            }
            Log.d(TAG, "CDATA " + getFormatDate(date, "yyyy-MM-dd"));
            date = addDays(date, 1);
            index++;
        }
    }

    public void getContentForDay(int channel, Date date) {
        String otime = "00:00";
        String vdirection = String.format(STR_SCHEDULECHANNEL, channel, getFormatDate(date, "yyyy-MM-dd"));
        Log.d(TAG, HOST + vdirection);
        org.jsoup.nodes.Document doc = new HttpContent(HOST + vdirection).getDocument();
        Elements items = doc.select(STR_ELMDOCSELECT);
        for (org.jsoup.nodes.Element item : items){
            String etime = item.html();
            Date startDate = null;
            Date endDate = null;
            if (Integer.parseInt(etime.split(":")[0]) < Integer.parseInt(otime.split(":")[0])) {
                addDays(date, 1);
            }
            otime = etime;
            startDate = getDateFromString(getFormatDate(date, "yyyy-MM-dd") + " " + etime + ":00", "yyyy-MM-dd  HH:mm:ss");
            endDate = getDateFromString(getFormatDate(date, "yyyy-MM-dd") + " " + "23:59:59", "yyyy-MM-dd  HH:mm:ss");
            String etitle = "";
            String efulldescurl = "";
            String edesc = "";
            try {
                Elements titleItem = item.nextElementSibling().select(STR_ELMDOCTITLE);
                if (titleItem != null) {
                    efulldescurl = titleItem.select("a").attr("href");
                    etitle = titleItem.text();
                }
            } catch (Exception e) {
                //
            }
            try {
                Elements descItem = item.nextElementSibling().nextElementSibling().select(STR_ELMDOCDESC);
                if (descItem != null) {
                    edesc = descItem.text();
                }
            } catch (Exception e) {
                //
            }
            TVProgram program = new TVProgram(-1, channel, startDate, endDate, etitle);
            program.setCorrectionTime(DEF_CORRECTION);
            getCategoryFromTitle(program);
            //Log.d(TAG, program.toString());

            if (edesc.length() > 0 && !Objects.equals(edesc, "")) {
                program.setDescription(edesc);
            }
            if (efulldescurl.length() > 0 && !Objects.equals(efulldescurl, "") && mFullDesc) {
                program.setUrlFullDesc(efulldescurl);
                getFullDesc(program);
            }
            mPrograms.add(program);
        }

    }

    @Override
    protected Void doInBackground(Integer... values) {
        int type_channels = values[0];
        index = 0;
        progress = new String[] {"", "", ""};
        mPrograms = mDataSource.getPrograms(0, type_channels, new Date());
        mPrograms.clear();
        if (type_channels == -1) {
            mChannels = mDataSource.getChannels(true, 0);
            total = mChannels.size() * mDataSource.getCoutDays();
            for (TVChannel channel : mChannels.getData()) {
                progress[0] = channel.getName();
                getContentForChannel(channel.getIndex(), new Date());
            }
        } else {
            total = 1 * mDataSource.getCoutDays();
            progress[0] = "-1";
            getContentForChannel(type_channels, new Date());
        }
        mPrograms.setProgramStop();
        progress[0] = "0";
        progress[1] = "";
        progress[2] = String.valueOf(counter);
        publishProgress(progress);
        mPrograms.saveToDB();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        mListeners.onUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mListeners.onStop();
    }

    private void getCategoryFromTitle(TVProgram program) {
        //
    }

    private void getFullDesc(TVProgram program) {
        //
    }
}
