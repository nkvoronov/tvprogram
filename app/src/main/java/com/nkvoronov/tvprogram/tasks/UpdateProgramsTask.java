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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
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

    public void setFullDesc(boolean fullDesc) {
        mFullDesc = fullDesc;
    }

    @Override
    protected void onPreExecute() {
        mListeners.onStart();
    }

    public void getContentForChannel(int channel, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        Calendar calendar_last = Calendar.getInstance();
        calendar.setTime(date);
        calendar_last.setTime(date);
        calendar_last.add(Calendar.DATE, mDataSource.getCoutDays());
        Log.d(TAG, "LAST " + dateFormat.format(calendar_last.getTime()));
        while (!calendar.getTime().equals(calendar_last.getTime())) {
            progress[1] = dateFormat.format(calendar.getTime());
            getContentForDay(channel, calendar.getTime());
            counter = (int) (((index+1) / (float) total) * 100);
            progress[2] = String.valueOf(counter);
            publishProgress(progress);
            if(isCancelled()){
                break;
            }
            Log.d(TAG, "CDATA " + dateFormat.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 1);
            index++;
        }
    }

    public void getContentForDay(int channel, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String otime = "00:00";
        String direction = String.format(STR_SCHEDULECHANNEL, channel, dateFormat.format(date));
        org.jsoup.nodes.Document doc = new HttpContent(HOST + direction).getDocument();
        Elements items = doc.select(STR_ELMDOCSELECT);
        for (org.jsoup.nodes.Element item : items){
            String etime = item.html().trim();
            Date startDate = null;
            Date endDate = null;
            if (Integer.parseInt(etime.split(":")[0]) < Integer.parseInt(otime.split(":")[0])) {
                calendar.add(Calendar.DATE, 1);
            }
            otime = etime;
            try {
                startDate = dateTimeFormat.parse(dateFormat.format(calendar.getTime()) + " " + etime + ":00");
                endDate = dateTimeFormat.parse(dateFormat.format(calendar.getTime()) + " 23:59:59");
            } catch (ParseException e) {
                e.fillInStackTrace();
                Log.d(TAG, e.getMessage());
            }
            Log.d(TAG, "times " + dateTimeFormat.format(startDate) + " / " + dateTimeFormat.format(endDate));
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
                mPrograms.preUpdateProgram(channel.getIndex());
                getContentForChannel(channel.getIndex(), new Date());
            }
        } else {
            total = 1 * mDataSource.getCoutDays();
            progress[0] = "-1";
            mPrograms.preUpdateProgram(type_channels);
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
