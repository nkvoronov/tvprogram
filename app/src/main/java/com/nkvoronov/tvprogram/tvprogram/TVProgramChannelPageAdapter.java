package com.nkvoronov.tvprogram.tvprogram;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TVProgramChannelPageAdapter extends FragmentPagerAdapter {
    private final Context mContext;
    private Date mDate;
    int mDays;

    public TVProgramChannelPageAdapter(Context context, FragmentManager fm, Date date, int days) {
        super(fm);
        mContext = context;
        mDate = date;
        mDays = days;
    }

    @Override
    public Fragment getItem(int position) {
        TVProgramChannelFragment page = TVProgramChannelFragment.newInstance(position);
        return page;
    }

    @Override
    public int getCount() {
        return  mDays;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        calendar.add(Calendar.DATE, position);
        return simpleDateFormat.format(calendar.getTime());
    }
}
