package com.nkvoronov.tvprogram.tasks;

import java.util.Date;
import android.util.Log;
import java.util.Calendar;
import android.os.AsyncTask;
import java.text.ParseException;
import org.jsoup.select.Elements;
import java.text.SimpleDateFormat;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.tvschedule.TVSchedule;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleDescription;
import com.nkvoronov.tvprogram.tvschedule.TVSchedulesList;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleCategory;
import static com.nkvoronov.tvprogram.common.HttpContent.HOST;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleCategoriesList;
import static com.nkvoronov.tvprogram.common.MainDataSource.TAG;

public class UpdateSchedulesTask extends AsyncTask<Integer,String,Void> {
    public static final int DEF_CORRECTION = 120;
    public static final String STR_SCHEDULECHANNEL = "schedule_channel_%d_day_%s.html";
    public static final String STR_ELMDOCSELECT = "div[class~=(?:pasttime|onair|time)]";
    public static final String STR_ELMDOCTITLE = "div[class~=(?:pastprname2|prname2)]";
    public static final String STR_ELMDOCDESC = "div[class~=(?:pastdesc|prdesc)]";

    private int index;
    private int total;
    private int counter;
    private String[] progress;
    private TVChannelsList mChannels;
    private TVSchedulesList mPrograms;
    private OnTaskListeners mListeners;
    private MainDataSource mDataSource;

    public UpdateSchedulesTask(MainDataSource dataSource) {
        mDataSource = dataSource;
        mChannels = null;
        index = 0;
    }

    public interface OnTaskListeners {
        public void onStart();
        public void onUpdate(String[] progress);
        public void onStop();
    }

    public void setListeners(OnTaskListeners listeners) {
        mListeners = listeners;
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
                e.fillInStackTrace();
                Log.d(TAG, e.getMessage());
            }
            try {
                Elements descItem = item.nextElementSibling().nextElementSibling().select(STR_ELMDOCDESC);
                if (descItem != null) {
                    edesc = descItem.text();
                }
            } catch (Exception e) {
                e.fillInStackTrace();
                Log.d(TAG, e.getMessage());
            }
            TVSchedule schedule = new TVSchedule(-1, channel, startDate, endDate, etitle);
            schedule.setFavorites(false);
            getCategoryFromTitle(schedule);

            if (edesc.length() > 0 && !edesc.equals("")) {
                if (schedule.getDescription() == null) {
                    schedule.setDescription(new TVScheduleDescription(""));
                }
                schedule.getDescription().setDescription(edesc);
            }

            if (efulldescurl.length() > 0 && !efulldescurl.equals("") && mDataSource.isFullDesc()) {
                if (schedule.getDescription() == null) {
                    schedule.setDescription(new TVScheduleDescription(""));
                }
                schedule.getDescription().setUrlFullDesc(efulldescurl);
                getFullDesc(schedule);
            }
            mPrograms.add(schedule);
        }
    }

    @Override
    protected Void doInBackground(Integer... values) {
        int type_channels = values[0];
        index = 0;
        progress = new String[] {"", "", ""};
        mPrograms = mDataSource.getSchedules(0, String.valueOf(type_channels), new Date());
        mPrograms.clear();
        if (type_channels == -1) {
            mChannels = mDataSource.getChannels(true, 0);
            total = mChannels.size() * mDataSource.getCoutDays();
            for (TVChannel channel : mChannels.getData()) {
                progress[0] = channel.getName();
                mPrograms.preUpdateSchedules(channel.getIndex());
                getContentForChannel(channel.getIndex(), new Date());
            }
        } else {
            total = 1 * mDataSource.getCoutDays();
            progress[0] = "-1";
            mPrograms.preUpdateSchedules(type_channels);
            getContentForChannel(type_channels, new Date());
        }
        mPrograms.setScheduleEnding();
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

    private Boolean titleContainsDictWorlds(String title, String dictionary) {
        Boolean res = false;
        String[] list = dictionary.split(",");
        for (String str:list) {
            res = res || title.contains(str);
        }
        return res;
    }

    private void getCategoryFromTitle(TVSchedule schedule) {
        TVScheduleCategoriesList categories = mDataSource.getCategories();
        for (TVScheduleCategory category : categories.getData()) {
            if (titleContainsDictWorlds(schedule.getTitle().toLowerCase(), category.getDictionary())) {
                schedule.setCategory(category.getId());
                break;
            }
        }
    }

    private void getFullDesc(TVSchedule schedule) {
        if (schedule.getDescription() != null) {
            //
        }
    }
}