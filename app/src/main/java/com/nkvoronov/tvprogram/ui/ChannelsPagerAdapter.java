package com.nkvoronov.tvprogram.ui;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.nkvoronov.tvprogram.R;

public class ChannelsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_C_TITLES = new int[]{R.string.tab_all, R.string.tab_favorites};
    private final Context mContext;

    public ChannelsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        PageChannels page = PageChannels.newInstance(position);
        return page;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_C_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_C_TITLES.length;
    }
}
