package com.nkvoronov.tvprogram.tvschedule;

import android.os.Bundle;
import android.view.View;
import android.text.Editable;
import android.view.ViewGroup;
import android.content.Intent;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.text.TextWatcher;
import android.graphics.Typeface;
import com.nkvoronov.tvprogram.R;
import android.view.LayoutInflater;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import com.nkvoronov.tvprogram.common.MainDataSource;
import androidx.recyclerview.widget.LinearLayoutManager;
import static com.nkvoronov.tvprogram.common.DateUtils.*;

public class TVScheduleSearchFragment extends Fragment {
    private static final String ARG_SEARCH_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvschedule.page_number_search";

    private int mPageIndex;
    private TextView mEmptyTextView;
    private EditText mSearchEditText;
    private ScheduleAdapter mAdapter;
    private String mSearchString = "";
    private RecyclerView mScheduleView;
    private MainDataSource mDataSource;

    public static TVScheduleSearchFragment newInstance(int index) {
        TVScheduleSearchFragment fragment = new TVScheduleSearchFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SEARCH_PAGE_NUMBER, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_SEARCH_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvschedules_search, container, false);
        mDataSource = MainDataSource.get(getContext());
        mSearchEditText = root.findViewById(R.id.edt_search);
        mSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mSearchString = charSequence.toString();
                updateUI();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });
        mScheduleView = root.findViewById(R.id.tvschedule_view);
        mEmptyTextView = root.findViewById(R.id.tvschedule_empty_search);
        mScheduleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        if (mSearchString == null) {
            mSearchString = "";
        }
        TVSchedulesList schedules = mDataSource.getSchedules(3, mSearchString, null);
        if (mAdapter == null) {
            mAdapter = new TVScheduleSearchFragment.ScheduleAdapter(schedules);
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

            if (mSchedule.isFavoritesChannel()) {
                mChannelFavorites.setImageResource(R.drawable.favorites_on);
            }
            else {
                mChannelFavorites.setImageResource(R.drawable.favorites_off);
            }

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

            mStart.setText(getDateFormat(mSchedule.getStarting(), "EEE, d MMM HH:mm"));
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
            TVSchedule Schedule =  mSchedulesList.get(position);
            holder.bind(Schedule);
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
