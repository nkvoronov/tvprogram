package com.nkvoronov.tvprogram.tvschedule;

import java.util.Date;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.nkvoronov.tvprogram.R;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.common.MainDataSource;

public class TVScheduleChannelActivity extends AppCompatActivity {
    private static final String EXTRA_TVCHANNEL_INDEX = "com.nkvoronov.tvprogram.tvschedule.tvchannel_index";

    TVChannel mChannel;
    private int mChannelIndex;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private TextView mChannelName;
    private ImageView mChannelIcon;
    private ImageView mChannelFavorites;
    private MainDataSource mDataSource;
    private TVScheduleChannelPageAdapter mPageAdapter;

    public static Intent newIntent(Context context, int channel_index) {
        Intent intent = new Intent(context, TVScheduleChannelActivity.class);
        intent.putExtra(EXTRA_TVCHANNEL_INDEX, channel_index);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(EXTRA_TVCHANNEL_INDEX, mChannelIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvschedules_channel);
        mDataSource = MainDataSource.get(this);
        if (savedInstanceState != null) {
            mChannelIndex = (int) savedInstanceState.getSerializable(EXTRA_TVCHANNEL_INDEX);
        } else {
            mChannelIndex = (int) getIntent().getSerializableExtra(EXTRA_TVCHANNEL_INDEX);
        }
        mChannel = mDataSource.getChannel(mChannelIndex);
        mChannelIcon = findViewById(R.id.channel_icon_own);
        Glide
                .with(this)
                .load(mDataSource.getChannelIconFile(mChannel.getIndex()))
                .fitCenter()
                .into(mChannelIcon);
        mChannelName = findViewById(R.id.channel_name_own);
        mChannelName.setText(mChannel.getName());
        mChannelFavorites = findViewById(R.id.channel_favorites_own);
        setFavorites();
        mChannelFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mChannel.changeFavorites();
                setFavorites();
            }
        });
        mTabLayout = findViewById(R.id.tvschedulechannels_tabs);
        mViewPager = findViewById(R.id.tvschedulechannels_pager);

        long[] info = mDataSource.getScheduleInfo(mChannelIndex);
        Date beginDate = new Date(info[0]);
        int days = (int)info[1];
        int current = (int)info[2];

        mPageAdapter = new TVScheduleChannelPageAdapter(this, getSupportFragmentManager(), mChannelIndex, beginDate, days);
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mPageAdapter.getCount(); i++) {
            if (i == current) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    private void setFavorites() {
        if (mChannel.isFavorites()) {
            mChannelFavorites.setImageResource(R.drawable.favorites_on);
        }
        else {
            mChannelFavorites.setImageResource(R.drawable.favorites_off);
        }
    }
}
