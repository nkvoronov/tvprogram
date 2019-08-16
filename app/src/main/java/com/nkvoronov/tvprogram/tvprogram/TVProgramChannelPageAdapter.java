package com.nkvoronov.tvprogram.tvprogram;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.Date;
import static com.nkvoronov.tvprogram.common.DateUtils.*;

public class TVProgramChannelPageAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    private int mChannelIndex;
    private Date mDate;
    int mDays;

    public TVProgramChannelPageAdapter(Context context, FragmentManager fm, int index, Date date, int days) {
        super(fm);
        mContext = context;
        mChannelIndex = index;
        mDate = date;
        mDays = days;
    }

    @Override
    public Fragment getItem(int position) {
        TVProgramChannelFragment page = TVProgramChannelFragment.newInstance(position, mChannelIndex, addDays(mDate, position));
        return page;
    }

    @Override
    public int getCount() {
        return  mDays;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getFormatDate(addDays(mDate, position), "EEE, d MMM");
    }
}
