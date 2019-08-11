package com.nkvoronov.tvprogram.tvprogram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVProgramChannelActivity extends AppCompatActivity {
    private TVProgramChannelPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TVProgramDataSource mDataSource;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, TVProgramChannelActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvchannels);
        mDataSource = TVProgramDataSource.get(this);
        mTabLayout = findViewById(R.id.tvprogramchannels_tabs);
        mViewPager = findViewById(R.id.tvprogramchannels_pager);
        mPageAdapter = new TVProgramChannelPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
