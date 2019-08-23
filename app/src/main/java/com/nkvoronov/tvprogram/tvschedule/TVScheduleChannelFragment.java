package com.nkvoronov.tvprogram.tvschedule;

import java.util.Date;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.graphics.Color;
import android.widget.TextView;
import android.widget.ImageView;
import com.nkvoronov.tvprogram.R;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.nkvoronov.tvprogram.common.MainDataSource;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import androidx.recyclerview.widget.LinearLayoutManager;

public class TVScheduleChannelFragment extends Fragment {
    private static final String ARG_TVSCHEDULE_DATE = "com.nkvoronov.tvprogram.tvschedule.date";
    private static final String ARG_TVPROGRAM_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvschedule.page_number";
    private static final String ARG_TVPROGRAM_CHANNEL_INDEX = "com.nkvoronov.tvprogram.tvschedule.channel_index";

    private int mPageIndex;
    private int mChannelIndex;
    private Date mScheduleDate;
    private TextView mEmptyTextView;
    private RecyclerView mScheduleView;
    private ScheduleChannelAdapter mAdapter;
    private MainDataSource mDataSource;

    public static TVScheduleChannelFragment newInstance(int page, int channel, Date date) {
        TVScheduleChannelFragment fragment = new TVScheduleChannelFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TVPROGRAM_PAGE_NUMBER, page);
        args.putSerializable(ARG_TVPROGRAM_CHANNEL_INDEX, channel);
        args.putSerializable(ARG_TVSCHEDULE_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_TVPROGRAM_PAGE_NUMBER);
        mChannelIndex = (int) getArguments().getSerializable(ARG_TVPROGRAM_CHANNEL_INDEX);
        mScheduleDate = (Date) getArguments().getSerializable(ARG_TVSCHEDULE_DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvschedules_channel, container, false);
        mDataSource = MainDataSource.get(getContext());
        mScheduleView = root.findViewById(R.id.tvschedulechannel_view);
        mEmptyTextView = root.findViewById(R.id.tvschedulechannel_empty);
        mScheduleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        TVSchedulesList programs = mDataSource.getSchedules(0, String.valueOf(mChannelIndex), mScheduleDate);

        if (mAdapter == null) {
            mAdapter = new ScheduleChannelAdapter(programs);
            mScheduleView.setAdapter(mAdapter);
        } else {
            mAdapter.setScheduleList(programs);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ScheduleChannelHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private TextView mStart;
        private TextView mTitle;
        private TVSchedule mSchedule;
        private TextView mDuration;
        private ImageView mFavoriteIcon;
        private ImageView mCategoryIcon;

        public ScheduleChannelHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_tvschedules_channel, parent, false));
            itemView.setOnClickListener(this);
            mStart = itemView.findViewById(R.id.text_start);
            mDuration = itemView.findViewById(R.id.text_duration);
            mTitle = itemView.findViewById(R.id.text_title);
            mFavoriteIcon = itemView.findViewById(R.id.image_fav);
            mCategoryIcon = itemView.findViewById(R.id.image_category);

        }

        public void bind(TVSchedule schedule) {
            mSchedule = schedule;
            if (mSchedule.isFavorites()) {
                mFavoriteIcon.setVisibility(View.VISIBLE);
            } else {
                mFavoriteIcon.setVisibility(View.GONE);
            }

            mTitle.setText(mSchedule.getTitle());
            mTitle.setTypeface(null, Typeface.NORMAL);
            mTitle.setTextColor(Color.BLACK);

            if (mSchedule.getTimeType() == 1) {
                mTitle.setTypeface(null, Typeface.BOLD);
            } else if (mSchedule.getTimeType() == 0) {
                mTitle.setTextColor(Color.GRAY);
                mTitle.setTypeface(null, Typeface.ITALIC);
            }

            mStart.setText(getDateFormat(mSchedule.getStarting(), "HH:mm"));
            mDuration.setText(getActivity().getString(R.string.dutation_txt, getDuration(mSchedule.getStarting(), mSchedule.getEnding())));

            if (mSchedule.getCategory() != 0) {
                mCategoryIcon.setVisibility(View.VISIBLE);
                mDataSource.setCategoryDrawable(mCategoryIcon, mSchedule.getCategory());
            } else {
                mCategoryIcon.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            openScheduleDetail();
        }

        private void openScheduleDetail() {
            Intent intent = TVScheduleDetailActivity.newIntent(getActivity(), mSchedule.getId(), mSchedule.getIndex());
            startActivity(intent);
        }
    }

    private class ScheduleChannelAdapter extends RecyclerView.Adapter<TVScheduleChannelFragment.ScheduleChannelHolder> {
        private TVSchedulesList mSchedulesList;

        public ScheduleChannelAdapter(TVSchedulesList schedules) {
            mSchedulesList = schedules;
        }

        @Override
        public ScheduleChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ScheduleChannelHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ScheduleChannelHolder holder, int position) {
            TVSchedule schedules =  mSchedulesList.get(position);
            holder.bind(schedules);
        }

        @Override
        public int getItemCount() {
            if (mSchedulesList.size() == 0) {
                mEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyTextView.setVisibility(View.INVISIBLE);
            }
            return mSchedulesList.size();
        }

        public void setScheduleList(TVSchedulesList schedules) {
            mSchedulesList = schedules;
        }
    }
}
