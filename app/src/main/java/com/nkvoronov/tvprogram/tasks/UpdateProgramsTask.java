package com.nkvoronov.tvprogram.tasks;

import java.util.Date;
import android.util.Log;
import java.util.Objects;
import java.util.Calendar;
import android.os.AsyncTask;
import java.text.ParseException;
import org.jsoup.select.Elements;
import java.text.SimpleDateFormat;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.tvprogram.TVProgram;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvprogram.TVProgramDescription;
import com.nkvoronov.tvprogram.tvprogram.TVProgramsList;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tvprogram.TVProgramCategory;
import static com.nkvoronov.tvprogram.common.HttpContent.HOST;
import com.nkvoronov.tvprogram.tvprogram.TVProgramCategoriesList;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;

public class UpdateProgramsTask extends AsyncTask<Integer,String,Void> {
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
    private TVProgramsList mPrograms;
    private OnTaskListeners mListeners;
    private TVProgramDataSource mDataSource;

    public UpdateProgramsTask(TVProgramDataSource dataSource) {
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
            TVProgram program = new TVProgram(-1, channel, startDate, endDate, etitle);
            program.setFavorites(false);
            getCategoryFromTitle(program);

            if (edesc.length() > 0 && !edesc.equals("")) {
                if (program.getDescription() == null) {
                    program.setDescription(new TVProgramDescription(edesc));
                }
                program.getDescription().setShortDescription(edesc);
            }

            if (efulldescurl.length() > 0 && !efulldescurl.equals("")) {
                if (program.getDescription() == null) {
                    program.setDescription(new TVProgramDescription(""));
                }
                program.getDescription().setUrlFullDesc(efulldescurl);
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
        mPrograms = mDataSource.getPrograms(0, String.valueOf(type_channels), new Date());
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
        mPrograms.setProgramEnding();
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

    private void getCategoryFromTitle(TVProgram program) {
        TVProgramCategoriesList categories = mDataSource.getCategories();
        for (TVProgramCategory programCategory : categories.getData()) {
            if (titleContainsDictWorlds(program.getTitle().toLowerCase(), programCategory.getDictionary())) {
                program.setCategory(programCategory.getId());
                break;
            }
        }
    }

    private void getFullDesc(TVProgram program) {
        if (program.getDescription() != null) {
            //
        }
    }
}
