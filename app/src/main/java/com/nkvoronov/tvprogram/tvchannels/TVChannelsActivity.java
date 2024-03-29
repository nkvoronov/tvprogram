package com.nkvoronov.tvprogram.tvchannels;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.content.Context;
import com.nkvoronov.tvprogram.R;
import android.app.ProgressDialog;
import androidx.core.view.MenuCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.tasks.UpdateChannelsTask;

public class TVChannelsActivity extends AppCompatActivity implements TVChannelsFragment.ChangesChannels {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    ProgressDialog mProgressDialog;
    private MainDataSource mDataSource;
    private UpdateChannelsTask mUpdateTask;
    private TVChannelsPageAdapter mPageAdapter;

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
        mUpdateTask.execute();
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, TVChannelsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvchannels);
        mDataSource = MainDataSource.get(this);
        mTabLayout = findViewById(R.id.tvschedulechannels_tabs);
        mViewPager = findViewById(R.id.tvschedulechannels_pager);
        mPageAdapter = new TVChannelsPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(getString(R.string.prg_update_caption));
        mUpdateTask = new UpdateChannelsTask(mDataSource);
        mUpdateTask.setListeners(new UpdateChannelsTask.OnTaskListeners() {
            @Override
            public void onStart() {
                mProgressDialog.setMessage(getString(R.string.channel_progress_msg));
                mProgressDialog.show();
            }

            @Override
            public void onUpdate(String[] progress) {
                mProgressDialog.setMessage(getString(R.string.update_channel, progress[0]));
                mProgressDialog.setProgress(Integer.parseInt(progress[1]));
            }

            @Override
            public void onStop() {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                onUpdatePages(null);
            }
        });
    }

    @Override
    public void onUpdatePages(TVChannel channel) {
        for (int i = 0; i < mPageAdapter.getCount(); i++) {
            ((TVChannelsFragment) mPageAdapter.instantiateItem(mViewPager, i)).updateUI();
        }
    }
}
