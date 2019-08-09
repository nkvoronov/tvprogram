package com.nkvoronov.tvprogram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.common.ChannelList;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.ui.PageChannels;

public class ChannelsActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TVProgramDataSource mDataSource;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_channels, menu);
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
        PageChannels page;
        ChannelList channelList = new ChannelList(this, mDataSource.getLang(), mDataSource.isIndexSort());
        channelList.loadFromNetAndUpdate(true);
//        for (int i = 0; i < mChannelsPagerAdapter.getCount(); i++) {
//            page = (PageChannels) mChannelsPagerAdapter.getItem(i);
//            page.updateUI();
//        }
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ChannelsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channels);

        mDataSource = TVProgramDataSource.get(this);

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = findViewById(R.id.pager);
        FragmentManager fragmentActivity = getSupportFragmentManager();


        mViewPager.setAdapter(new FragmentPagerAdapter(fragmentActivity) {
            @StringRes
            private final int[] TAB_C_TITLES = new int[]{R.string.tab_all, R.string.tab_favorites};

            @Override
            public int getCount() {
                return TAB_C_TITLES.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getResources().getString(TAB_C_TITLES[position]);
            }

            @Override
            public Fragment getItem(int position) {
                PageChannels page = PageChannels.newInstance(position);
                return page;
            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
    }
}
