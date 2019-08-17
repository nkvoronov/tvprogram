package com.nkvoronov.tvprogram.tvprogram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.tvchannels.TVChannelsList;
import java.util.Date;

public class TVProgramChannelActivity extends AppCompatActivity {
    private static final String EXTRA_TVCHANNEL_INDEX = "com.nkvoronov.tvprogram.tvprogram.tvchannel_index";
    private TVProgramChannelPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    TVChannel mChannel;
    private ImageView mChannelIcon;
    private TextView mChannelName;
    private ImageView mChannelFavorites;
    private TVProgramDataSource mDataSource;

    public static Intent newIntent(Context context, int channel_index) {
        Intent intent = new Intent(context, TVProgramChannelActivity.class);
        intent.putExtra(EXTRA_TVCHANNEL_INDEX, channel_index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvprogramchannel);
        mDataSource = TVProgramDataSource.get(this);
        int channel_index = (int) getIntent().getSerializableExtra(EXTRA_TVCHANNEL_INDEX);
        getChannel(channel_index);
        mChannelIcon = findViewById(R.id.channel_icon_own);
        Glide
                .with(this)
                .load(mChannel.getIconFile())
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
        mTabLayout = findViewById(R.id.tvprogramchannels_tabs);
        mViewPager = findViewById(R.id.tvprogramchannels_pager);
        mPageAdapter = new TVProgramChannelPageAdapter(this, getSupportFragmentManager(), mChannel.getIndex(), new Date(), mDataSource.getCoutDays());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private void getChannel(int index) {
        TVChannelsList channelList = mDataSource.getChannels(false, 0);
        mChannel = channelList.getForIndex(index);
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
