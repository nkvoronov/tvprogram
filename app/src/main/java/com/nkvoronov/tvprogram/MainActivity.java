package com.nkvoronov.tvprogram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.tabs.TabLayout;
import androidx.core.view.MenuCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.ui.ProgramsPagerAdapter;

public class MainActivity extends AppCompatActivity {
    ProgramsPagerAdapter mMainPagerAdapter;
    ViewPager mMainViewPager;
    TabLayout mMainTabLayout;

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
        Intent intent = ChannelsActivity.newIntent(this);
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
        mMainTabLayout = findViewById(R.id.main_tabs);
        mMainTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mMainPagerAdapter = new ProgramsPagerAdapter(this, getSupportFragmentManager());
        mMainViewPager = findViewById(R.id.main_pager);
        mMainViewPager.setAdapter(mMainPagerAdapter);
        mMainTabLayout.setupWithViewPager(mMainViewPager);
    }
}