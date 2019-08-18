package com.nkvoronov.tvprogram.tvprogram;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nkvoronov.tvprogram.R;

public class TVProgramPageAdapter extends FragmentStatePagerAdapter {

    @StringRes
    private final int[] TAB_P_TITLES = new int[]{R.string.tab_now, R.string.tab_search, R.string.tab_favorites};
    private final Context mContext;

    public TVProgramPageAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                TVProgramNowFragment page_now = TVProgramNowFragment.newInstance(0);
                return page_now;
            case 1:
                TVProgramSearchFragment page_search = TVProgramSearchFragment.newInstance(2);
                return page_search;
            case 2:
                TVProgramFavoritesFragment page_favorites = TVProgramFavoritesFragment.newInstance(3);
                return page_favorites;
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
