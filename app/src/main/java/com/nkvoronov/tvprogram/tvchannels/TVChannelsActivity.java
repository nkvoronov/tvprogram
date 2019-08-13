package com.nkvoronov.tvprogram.tvchannels;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.HttpContent;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import static com.nkvoronov.tvprogram.common.HttpContent.CHANNELS_PRE;
import static com.nkvoronov.tvprogram.common.HttpContent.ICONS_PRE;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.ALL_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.RUS_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.UKR_LANG;

public class TVChannelsActivity extends AppCompatActivity implements TVChannelsFragment.ChangesChannels {
    private static final String CHANNELS_SELECT = "option[value^=channel_]";

    private TVChannelsPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    ProgressDialog mProgressDialog;
    private TVProgramDataSource mDataSource;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tvchannels, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_update:
                onUpdate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onUpdate() {
        new updateChannelsTask().execute();
        onUpdatePages(null);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, TVChannelsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvchannels);
        mDataSource = TVProgramDataSource.get(this);
        mTabLayout = findViewById(R.id.tvprogramchannels_tabs);
        mViewPager = findViewById(R.id.tvprogramchannels_pager);
        mPageAdapter = new TVChannelsPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(getString(R.string.title_update_channels));
    }

    @Override
    public void onUpdatePages(TVChannel channel) {
        for (int i = 0; i < mPageAdapter.getCount(); i++) {
            ((TVChannelsFragment) mPageAdapter.instantiateItem(mViewPager, i)).updateUI();
        }
    }

    public class updateChannelsTask extends AsyncTask<Void,Integer,Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage(getString(R.string.channel_progress_msg));
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
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
                channel.setIsUpdate(false);
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
            channels.sort(true);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressDialog.setProgress(values[0]);
        }
    }
}
