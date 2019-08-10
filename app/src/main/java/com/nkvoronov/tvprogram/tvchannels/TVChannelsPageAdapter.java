package com.nkvoronov.tvprogram.tvchannels;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.nkvoronov.tvprogram.R;

public class TVChannelsPageAdapter extends FragmentPagerAdapter {

    @StringRes
    private final int[] TAB_C_TITLES = new int[]{R.string.tab_favorites, R.string.tab_all};
    private final Context mContext;

    public TVChannelsPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        TVChannelsFragment page = TVChannelsFragment.newInstance(position);
        return page;
    }

    @Override
    public int getCount() {
        return TAB_C_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_C_TITLES[position]);
    }
}
