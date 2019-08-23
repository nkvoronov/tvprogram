package com.nkvoronov.tvprogram.tvschedule;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import static com.nkvoronov.tvprogram.common.DateUtils.*;

public class TVScheduleChannelPageAdapter extends FragmentStatePagerAdapter {
    private Date mMinDate;
    private int mChannelIndex;
    private final Context mContext;
    private List<String> mListTabsName;

    public TVScheduleChannelPageAdapter(Context context, FragmentManager fm, int index, Date date, int count) {
        super(fm);
        mContext = context;
        mChannelIndex = index;
        mMinDate = date;
        mListTabsName = new ArrayList<>();
        for (int i=0 ; i < count ; i++) {
            String title = getDateFormat(addDays(mMinDate, i), "EEE, d MMM");
            mListTabsName.add(title);
        }
    }

    @Override
    public Fragment getItem(int position) {
        TVScheduleChannelFragment page = TVScheduleChannelFragment.newInstance(position, mChannelIndex, addDays(mMinDate, position));
        return page;
    }

    @Override
    public int getCount() {
        return  mListTabsName.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mListTabsName.get(position);
    }
}
