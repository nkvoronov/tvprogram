package com.nkvoronov.tvprogram;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;
import android.view.MenuItem;
import android.content.Intent;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import androidx.core.view.MenuCompat;
import android.content.DialogInterface;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.common.MainDataSource;
import com.nkvoronov.tvprogram.tasks.UpdateSchedulesTask;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsActivity;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleNowFragment;
import com.nkvoronov.tvprogram.tvschedule.TVSchedulePageAdapter;
import com.nkvoronov.tvprogram.tvschedule.TVScheduleFavoritesFragment;

public class MainActivity extends AppCompatActivity {
    private ViewPager mMainViewPager;
    private TabLayout mMainTabLayout;
    private MainDataSource mDataSource;
    private ProgressDialog mProgressDialog;
    private UpdateSchedulesTask mUpdateTask;
    private TVSchedulePageAdapter mPageAdapter;

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
        if (mDataSource.checkFavoritesChannel()) {
            mUpdateTask.execute(-1);
        } else {
            onChannelsUpdate();
        }
    }

    private void onChannelsUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.prg_update_caption));
        builder.setMessage(getString(R.string.program_not_favorites));
        builder.setPositiveButton(getString(R.string.bt_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onChannels();
            }
        });
        builder.setNegativeButton(getString(R.string.bt_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), getString(R.string.bt_no), Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateUI() {
        updatePages();
    }

    public void updatePages() {
        ((TVScheduleNowFragment) mPageAdapter.instantiateItem(mMainViewPager, 0)).updateUI();
        ((TVScheduleFavoritesFragment) mPageAdapter.instantiateItem(mMainViewPager, 1)).updateUI();
        // !!!!!!!!!!
        //((TVScheduleSearchFragment) mPageAdapter.instantiateItem(mMainViewPager, 2)).updateUI();
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
        mDataSource = MainDataSource.get(this);
        mMainTabLayout = findViewById(R.id.tvschedule_tabs);
        mMainViewPager = findViewById(R.id.tvschedule_pager);
        mPageAdapter = new TVSchedulePageAdapter(this, getSupportFragmentManager());
        mMainViewPager.setAdapter(mPageAdapter);
        mMainTabLayout.setupWithViewPager(mMainViewPager);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle(getString(R.string.prg_update_caption));

        mUpdateTask = new UpdateSchedulesTask(mDataSource);
        mUpdateTask.setListeners(new UpdateSchedulesTask.OnTaskListeners() {

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