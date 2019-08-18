package com.nkvoronov.tvprogram.tvprogram;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.nkvoronov.tvprogram.common.DateUtils.*;

public class TVProgramChannelPageAdapter extends FragmentStatePagerAdapter {
    private final Context mContext;
    private List<String> mListTabsName;
    private int mChannelIndex;

    public TVProgramChannelPageAdapter(Context context, FragmentManager fm, int index, Date date, int count) {
        super(fm);
        mContext = context;
        mChannelIndex = index;
        mListTabsName = new ArrayList<>();
        for (int i=0 ; i < count ; i++) {
            String title = getDateFormat(addDays(date, i), "EEE, d MMM");
            mListTabsName.add(title);
        }
    }

    @Override
    public Fragment getItem(int position) {
        TVProgramChannelFragment page = TVProgramChannelFragment.newInstance(position, mChannelIndex);
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
