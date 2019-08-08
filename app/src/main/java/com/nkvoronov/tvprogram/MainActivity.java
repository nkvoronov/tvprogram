package com.nkvoronov.tvprogram;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.StringRes;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.ui.PageFavoritesProgram;
import com.nkvoronov.tvprogram.ui.PageNextProgram;
import com.nkvoronov.tvprogram.ui.PageNowProgram;
import com.nkvoronov.tvprogram.ui.PageSearchProgram;

public class MainActivity extends AppCompatActivity {
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

        mMainViewPager = findViewById(R.id.main_pager);
        FragmentManager fragmentActivity = getSupportFragmentManager();

        mMainViewPager.setAdapter(new FragmentPagerAdapter(fragmentActivity) {
            @StringRes
            private final int[] TAB_P_TITLES = new int[]{R.string.tab_now, R.string.tab_next, R.string.tab_search, R.string.tab_favorites};

            @Override
            public int getCount() {
                return TAB_P_TITLES.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getResources().getString(TAB_P_TITLES[position]);
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        PageNowProgram page_now = PageNowProgram.newInstance(0);
                        return page_now;
                    case 1:
                        PageNextProgram page_next = PageNextProgram.newInstance(1);
                        return page_next;
                    case 2:
                        PageSearchProgram page_search = PageSearchProgram.newInstance(2);
                        return page_search;
                    case 3:
                        PageFavoritesProgram page_favorites = PageFavoritesProgram.newInstance(3);
                        return page_favorites;
                    default:
                        return null;
                }
            }
        });

        mMainTabLayout.setupWithViewPager(mMainViewPager);
    }
}