package com.nkvoronov.tvprogram.ui;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.nkvoronov.tvprogram.R;

public class ProgramsPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_P_TITLES = new int[]{R.string.tab_now, R.string.tab_next, R.string.tab_search, R.string.tab_favorites};
    private final Context mContext;

    public ProgramsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
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

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_P_TITLES[position]);
    }

    @Override
    public int getCount() {
        return TAB_P_TITLES.length;
    }
}