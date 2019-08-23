package com.nkvoronov.tvprogram.tasks;

import android.os.AsyncTask;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import com.nkvoronov.tvprogram.common.MainDataSource;
import static com.nkvoronov.tvprogram.common.HttpContent.ICONS_PRE;
import static com.nkvoronov.tvprogram.common.HttpContent.CHANNELS_PRE;
import static com.nkvoronov.tvprogram.common.MainDataSource.ALL_LANG;
import static com.nkvoronov.tvprogram.common.MainDataSource.RUS_LANG;
import static com.nkvoronov.tvprogram.common.MainDataSource.UKR_LANG;

public class UpdateChannelsTask extends AsyncTask<Void,String,Void> {
    private static final String CHANNELS_SELECT = "option[value^=channel_]";

    private OnTaskListeners mListeners;
    private MainDataSource mDataSource;

    public UpdateChannelsTask(MainDataSource dataSource) {
        mDataSource = dataSource;
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

    @Override
    protected Void doInBackground(Void... voids) {
        String lang = ALL_LANG;
        String[] progress;
        int counter;
        TVChannelsList channels = mDataSource.getChannels(false, 0);
        channels.clear();
        channels.preUpdateChannel();
        org.jsoup.nodes.Document document = new HttpContent(CHANNELS_PRE).getDocument();
        org.jsoup.select.Elements elements = document.select(CHANNELS_SELECT);
        int i = 0;
        int total = elements.size();
        for (org.jsoup.nodes.Element element : elements){
            String channel_name = element.text();
            if (channel_name.endsWith("(на укр.)")) {
                lang = UKR_LANG;
            } else {
                lang = RUS_LANG;
            }
            String channel_link = element.attr("value");
            String channel_index = channel_link.split("_")[1];
            String channel_icon = ICONS_PRE + channel_index + ".gif";
            TVChannel channel = new TVChannel(Integer.parseInt(channel_index), channel_name, channel_icon);
            channel.setLang(lang);
            channel.setIsFavorites(false);
            channels.add(channel);
            channels.saveChannelToDB(channel);
            channel.saveIconToFile();
            counter = (int) (((i+1) / (float) total) * 100);
            progress = new String[] {channel_name, String.valueOf(counter)};
            publishProgress(progress);
            if(isCancelled()){
                break;
            }
            i++;
        }
        channels.postUpdateChannel();
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
}
