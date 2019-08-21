package com.nkvoronov.tvprogram.tvprogram;

import java.util.Date;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVProgramChannelFragment  extends Fragment {
    private static final String ARG_TVPROGRAM_DATE = "com.nkvoronov.tvprogram.tvprogram.date";
    private static final String ARG_TVPROGRAM_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvprogram.page_number";
    private static final String ARG_TVPROGRAM_CHANNEL_INDEX = "com.nkvoronov.tvprogram.tvprogram.channel_index";

    private int mPageIndex;
    private int mChannelIndex;
    private Date mProgramDate;
    private TextView mEmptyTextView;
    private RecyclerView mProgramView;
    private ProgramChannelAdapter mAdapter;
    private TVProgramDataSource mDataSource;

    public static TVProgramChannelFragment newInstance(int page, int channel, Date date) {
        TVProgramChannelFragment fragment = new TVProgramChannelFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TVPROGRAM_PAGE_NUMBER, page);
        args.putSerializable(ARG_TVPROGRAM_CHANNEL_INDEX, channel);
        args.putSerializable(ARG_TVPROGRAM_DATE, date);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_TVPROGRAM_PAGE_NUMBER);
        mChannelIndex = (int) getArguments().getSerializable(ARG_TVPROGRAM_CHANNEL_INDEX);
        mProgramDate = (Date) getArguments().getSerializable(ARG_TVPROGRAM_DATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvprograms_channel, container, false);
        mDataSource = TVProgramDataSource.get(getContext());
        mProgramView = root.findViewById(R.id.tvprogramchannel_view);
        mEmptyTextView = root.findViewById(R.id.tvprogramchannel_empty);
        mProgramView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        TVProgramsList programs = mDataSource.getPrograms(0, mChannelIndex, mProgramDate);

        if (mAdapter == null) {
            mAdapter = new ProgramChannelAdapter(programs);
            mProgramView.setAdapter(mAdapter);
        } else {
            mAdapter.setProgramList(programs);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ProgramChannelHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private TextView mStart;
        private TextView mTitle;
        private TVProgram mProgram;
        private TextView mDuration;
        private ImageView mFavoriteIcon;
        private ImageView mCategoryIcon;

        public ProgramChannelHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_tvprograms_channel, parent, false));
            itemView.setOnClickListener(this);
            mStart = itemView.findViewById(R.id.text_start);
            mDuration = itemView.findViewById(R.id.text_duration);
            mTitle = itemView.findViewById(R.id.text_title);
            mFavoriteIcon = itemView.findViewById(R.id.image_fav);
            mCategoryIcon = itemView.findViewById(R.id.image_category);

        }

        public void bind(TVProgram program) {
            mProgram = program;
            if (mProgram.isFavorites()) {
                mFavoriteIcon.setVisibility(View.VISIBLE);
            } else {
                mFavoriteIcon.setVisibility(View.GONE);
            }

            mTitle.setText(mProgram.getTitle());
            mTitle.setTypeface(null, Typeface.NORMAL);
            mTitle.setTextColor(Color.BLACK);

            if (mProgram.getTimeType() == 1) {
                mTitle.setTypeface(null, Typeface.BOLD);
            } else if (mProgram.getTimeType() == 0) {
                mTitle.setTextColor(Color.GRAY);
                mTitle.setTypeface(null, Typeface.ITALIC);
            }

            mStart.setText(getDateFormat(mProgram.getStart(), "HH:mm"));
            mDuration.setText(getActivity().getString(R.string.dutation_txt, getDuration(mProgram.getStart(), mProgram.getStop())));

            if (mProgram.getCategory() != 0) {
                mCategoryIcon.setVisibility(View.VISIBLE);
                mDataSource.setCategoryDrawable(mCategoryIcon, mProgram.getCategory());
            } else {
                mCategoryIcon.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            openProgramDetail();
        }

        private void openProgramDetail() {
            Intent intent = TVProgramDetailActivity.newIntent(getActivity(), mProgram.getId(), mProgram.getIndex());
            startActivity(intent);
        }
    }

    private class ProgramChannelAdapter extends RecyclerView.Adapter<TVProgramChannelFragment.ProgramChannelHolder> {
        private TVProgramsList mProgramsList;

        public ProgramChannelAdapter(TVProgramsList programs) {
            mProgramsList = programs;
        }

        @Override
        public ProgramChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ProgramChannelHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ProgramChannelHolder holder, int position) {
            TVProgram program =  mProgramsList.get(position);
            holder.bind(program);
        }

        @Override
        public int getItemCount() {
            if (mProgramsList.size() == 0) {
                mEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyTextView.setVisibility(View.INVISIBLE);
            }
            return mProgramsList.size();
        }

        public void setProgramList(TVProgramsList programs) {
            mProgramsList = programs;
        }
    }
}
