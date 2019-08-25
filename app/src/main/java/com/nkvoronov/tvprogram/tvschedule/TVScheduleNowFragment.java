package com.nkvoronov.tvprogram.tvschedule;

import java.util.List;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.nkvoronov.tvprogram.R;
import android.widget.AdapterView;
import android.view.LayoutInflater;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.nkvoronov.tvprogram.common.MainDataSource;
import androidx.recyclerview.widget.LinearLayoutManager;
import static com.nkvoronov.tvprogram.common.DateUtils.*;

public class TVScheduleNowFragment extends Fragment {
    private static final String ARG_NOW_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvschedule.page_number_now";
    private int mPageIndex;
    private int mCategoryID;
    private Spinner mSpinnerFilter;
    private ScheduleAdapter mAdapter;
    private TextView mEmptyTextView;
    private RecyclerView mScheduleView;
    private MainDataSource mDataSource;

    public static TVScheduleNowFragment newInstance(int index) {
        TVScheduleNowFragment fragment = new TVScheduleNowFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NOW_PAGE_NUMBER, index);
        fragment.setArguments(args);
        return fragment;
    }

    private void setDefCategories() {
        mCategoryID = 0;
        mSpinnerFilter.setSelection(mCategoryID);
    }

    private void getItemCategories(int index) {
        if (index != mCategoryID) {
            mCategoryID = index;
            updateUI();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_NOW_PAGE_NUMBER);
    }

    private void addSpinnerFilter(View view) {
        mSpinnerFilter = view.findViewById(R.id.tvschedule_filter);
        List<String> list = mDataSource.getCategoriesList();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerFilter.setAdapter(dataAdapter);
        mSpinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getItemCategories(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });
        setDefCategories();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvschedules, container, false);
        mDataSource = MainDataSource.get(getContext());
        addSpinnerFilter(root);
        mScheduleView = root.findViewById(R.id.tvschedule_view);
        mEmptyTextView = root.findViewById(R.id.tvschedule_empty);
        mScheduleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        TVSchedulesList schedules = mDataSource.getSchedules(1, String.valueOf(mCategoryID), null);
        if (mAdapter == null) {
            mAdapter = new TVScheduleNowFragment.ScheduleAdapter(schedules);
            mScheduleView.setAdapter(mAdapter);
        } else {
            mAdapter.setScheduleList(schedules);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ScheduleHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private TextView mStart;
        private TextView mTitle;
        private TVSchedule mSchedule;
        private TextView mDuration;
        private TextView mChannelName;
        private ImageView mChannelIcon;
        private ImageView mFavoriteIcon;
        private ImageView mCategoryIcon;
        private ImageView mChannelFavorites;

        public ScheduleHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_tvschedules, parent, false));
            itemView.setOnClickListener(this);

            mChannelIcon = itemView.findViewById(R.id.channel_icon);
            mChannelIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openScheduleChannel();
                }
            });

            mChannelName = itemView.findViewById(R.id.channel_name);
            mChannelName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openScheduleChannel();
                }
            });

            mChannelFavorites = itemView.findViewById(R.id.channel_favorites);
            mChannelFavorites.setVisibility(View.GONE);

            mStart = itemView.findViewById(R.id.text_start);
            mDuration = itemView.findViewById(R.id.text_duration);
            mTitle = itemView.findViewById(R.id.text_title);
            mFavoriteIcon = itemView.findViewById(R.id.image_fav);
            mCategoryIcon = itemView.findViewById(R.id.image_category);

        }

        public void bind(TVSchedule schedule) {
            mSchedule = schedule;

            mChannelName.setText(mSchedule.getNameChannel());
            Glide
                    .with(getContext())
                    .load(mDataSource.getChannelIconFile(mSchedule.getIndex()))
                    .fitCenter()
                    .into(mChannelIcon);

            if (mSchedule.isFavorites()) {
                mFavoriteIcon.setVisibility(View.VISIBLE);
            } else {
                mFavoriteIcon.setVisibility(View.GONE);
            }

            mTitle.setText(mSchedule.getTitle());
            mTitle.setTypeface(null, Typeface.BOLD);
            mTitle.setTextColor(Color.BLACK);

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
            Intent intent = TVScheduleDetailActivity.newIntent(getActivity(), mSchedule.getId());
            startActivity(intent);
        }

        private void openScheduleChannel() {
            Intent intent = TVScheduleChannelActivity.newIntent(getActivity(), mSchedule.getIndex());
            startActivity(intent);
        }
    }

    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleHolder> {
        private TVSchedulesList mSchedulesList;

        public ScheduleAdapter(TVSchedulesList schedules) {
            mSchedulesList = schedules;
        }

        @Override
        public ScheduleHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ScheduleHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ScheduleHolder holder, int position) {
            TVSchedule schedule =  mSchedulesList.get(position);
            holder.bind(schedule);
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
