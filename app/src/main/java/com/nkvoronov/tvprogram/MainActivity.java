package com.nkvoronov.tvprogram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.MenuCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsActivity;
import com.nkvoronov.tvprogram.tvprogram.TVProgramPageAdapter;

public class MainActivity extends AppCompatActivity {
    private TVProgramPageAdapter mPageAdapter;
    private ViewPager mMainViewPager;
    private TabLayout mMainTabLayout;
    private TVProgramDataSource mDataSource;

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
        //
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
    }
}