package com.nkvoronov.tvprogram.tvchannels;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVChannelsActivity extends AppCompatActivity{
    private TVChannelsPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
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

    private void updatePages() {
        for (int i = 0; i < mPageAdapter.getCount(); i++) {
            ((TVChannelsFragment) mPageAdapter.instantiateItem(mViewPager, i)).updateUI();
        }
    }

    private void onUpdate() {
        mDataSource.getChannelsFromNetAndUpdate(true);
        updatePages();
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

        mTabLayout = findViewById(R.id.tabs);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        mViewPager = findViewById(R.id.pager);
        mPageAdapter = new TVChannelsPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);

        mTabLayout.setupWithViewPager(mViewPager);
    }
}
