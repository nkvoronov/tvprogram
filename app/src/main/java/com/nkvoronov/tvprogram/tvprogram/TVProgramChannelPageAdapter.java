package com.nkvoronov.tvprogram.tvprogram;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TVProgramChannelPageAdapter extends FragmentPagerAdapter {
    private final Context mContext;

    public TVProgramChannelPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        return  null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }
}
