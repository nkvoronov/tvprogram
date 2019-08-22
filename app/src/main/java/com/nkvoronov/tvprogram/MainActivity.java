package com.nkvoronov.tvprogram;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.app.ProgressDialog;
import androidx.core.view.MenuCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tasks.UpdateProgramsTask;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsActivity;
import com.nkvoronov.tvprogram.tvprogram.TVProgramFavoritesFragment;
import com.nkvoronov.tvprogram.tvprogram.TVProgramNowFragment;
import com.nkvoronov.tvprogram.tvprogram.TVProgramPageAdapter;
import com.nkvoronov.tvprogram.tvprogram.TVProgramSearchFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager mMainViewPager;
    private TabLayout mMainTabLayout;
    private ProgressDialog mProgressDialog;
    private UpdateProgramsTask mUpdateTask;
    private TVProgramDataSource mDataSource;
    private TVProgramPageAdapter mPageAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_update:
                onUpdate();
                break;
            case R.id.mi_channels:
                onChannels();
                break;
            case R.id.mi_settings:
                onSettings();
                break;
            case R.id.mi_about:
                onAbout();
                break;
            case R.id.mi_exit:
                onExit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onUpdate() {
        //Check favorites channels !!!
        mUpdateTask.execute(-1);
    }

    private void updateUI() {
        updatePages();
    }

    public void updatePages() {
        ((TVProgramNowFragment) mPageAdapter.instantiateItem(mMainViewPager, 0)).updateUI();
        ((TVProgramFavoritesFragment) mPageAdapter.instantiateItem(mMainViewPager, 1)).updateUI();
        //((TVProgramSearchFragment) mPageAdapter.instantiateItem(mMainViewPager, 2)).updateUI();
    }

    private void onChannels() {
        Intent intent = TVChannelsActivity.newIntent(this);
        startActivity(intent);
    }

    private void onSettings() {
        Intent intent = SettingsActivity.newIntent(this);
        startActivity(intent);
    }

    private void onAbout() {
        Intent intent = AboutActivity.newIntent(this);
        startActivity(intent);
    }

    private void onExit() {
        finishAndRemoveTask();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDataSource = TVProgramDataSource.get(this);
        mMainTabLayout = findViewById(R.id.tvprogram_tabs);
        mMainViewPager = findViewById(R.id.tvprogram_pager);
        mPageAdapter = new TVProgramPageAdapter(this, getSupportFragmentManager());
        mMainViewPager.setAdapter(mPageAdapter);
        mMainTabLayout.setupWithViewPager(mMainViewPager);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(getString(R.string.prg_update_caption));

        mUpdateTask = new UpdateProgramsTask(mDataSource);
        mUpdateTask.setListeners(new UpdateProgramsTask.OnTaskListeners() {

            @Override
            public void onStart() {
                mProgressDialog.setMessage(getString(R.string.program_progress_msg));
                mProgressDialog.show();
            }

            @Override
            public void onUpdate(String[] progress) {
                if (progress[0].equals("0")) {
                    mProgressDialog.setMessage(getString(R.string.program_progress_msg2));
                } else {
                    mProgressDialog.setMessage(getString(R.string.program_progress_msg1, progress[0], progress[1]));
                }
                mProgressDialog.setProgress(Integer.parseInt(progress[2]));
            }

            @Override
            public void onStop() {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                updateUI();
            }
        });
    }
}