package com.nkvoronov.tvprogram.tasks;

import android.os.AsyncTask;
import android.util.Log;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import static com.nkvoronov.tvprogram.common.HttpContent.CHANNELS_PRE;
import static com.nkvoronov.tvprogram.common.HttpContent.ICONS_PRE;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.ALL_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.RUS_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.UKR_LANG;

public class UpdateChannelsTask extends AsyncTask<Void,Integer,Void> {
    private static final String CHANNELS_SELECT = "option[value^=channel_]";

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

    @Override
    protected Void doInBackground(Void... voids) {
        String channel_index;
        String channel_name;
        String channel_link;
        String channel_icon;
        String lang = ALL_LANG;
        Boolean flag = false;
        TVChannelsList channels = mDataSource.getChannels(false, 0);
        channels.clear();
        channels.preUpdateChannel();
        org.jsoup.nodes.Document document = new HttpContent(CHANNELS_PRE).getDocument();
        org.jsoup.select.Elements elements = document.select(CHANNELS_SELECT);
        int i = 0;
        int total = elements.size();
        for (org.jsoup.nodes.Element element : elements){
            channel_name = element.text();
            if (channel_name.endsWith("(на укр.)")) {
                lang = UKR_LANG;
            } else {
                lang = RUS_LANG;
            }
            channel_link = element.attr("value");
            channel_index = channel_link.split("_")[1];
            channel_icon = ICONS_PRE + channel_index + ".gif";
            TVChannel channel = new TVChannel(Integer.parseInt(channel_index), channel_name, channel_icon);
            channel.setLang(lang);
            channel.setIsFavorites(false);
            channels.add(channel);
            channel.setParent(channels);
            Log.d(TAG, channel.toString());
            channels.saveChannelToDB(channel);
            channel.saveIconToFile();
            publishProgress((int) (((i+1) / (float) total) * 100));
            if(isCancelled()){
                break;
            }
            i++;
        }
        channels.postUpdateChannel();
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
