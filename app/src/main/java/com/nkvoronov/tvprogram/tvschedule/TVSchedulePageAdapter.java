package com.nkvoronov.tvprogram.tvschedule;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nkvoronov.tvprogram.R;

public class TVSchedulePageAdapter extends FragmentStatePagerAdapter {

    @StringRes
    private final int[] TAB_P_TITLES = new int[]{R.string.tab_now, R.string.tab_favorites, R.string.tab_search};
    private final Context mContext;

    public TVSchedulePageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TVScheduleNowFragment page_now = TVScheduleNowFragment.newInstance(0);
                return page_now;
            case 1:
                TVScheduleFavoritesFragment page_favorites = TVScheduleFavoritesFragment.newInstance(1);
                return page_favorites;
            case 2:
                TVScheduleSearchFragment page_search = TVScheduleSearchFragment.newInstance(2);
                return page_search;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return TAB_P_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_P_TITLES[position]);
    }
}
