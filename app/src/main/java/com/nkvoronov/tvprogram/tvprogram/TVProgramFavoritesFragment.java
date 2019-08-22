package com.nkvoronov.tvprogram.tvprogram;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import android.graphics.Typeface;
import com.nkvoronov.tvprogram.R;
import android.view.LayoutInflater;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVProgramFavoritesFragment extends Fragment {
    private static final String ARG_FAVORITES_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvprogram.page_number_favorites";

    private int mPageIndex;
    private Spinner mSpinnerFilter;
    private ProgramAdapter mAdapter;
    private TextView mEmptyTextView;
    private RecyclerView mProgramView;
    private TVProgramDataSource mDataSource;

    public static TVProgramFavoritesFragment newInstance(int index) {
        TVProgramFavoritesFragment fragment = new TVProgramFavoritesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_FAVORITES_PAGE_NUMBER, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_FAVORITES_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvprograms, container, false);
        mDataSource = TVProgramDataSource.get(getContext());
        mSpinnerFilter = root.findViewById(R.id.tvprogram_filter);
        mSpinnerFilter.setVisibility(View.GONE);
        mProgramView = root.findViewById(R.id.tvprogram_view);
        mEmptyTextView = root.findViewById(R.id.tvprogram_empty);
        mProgramView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        TVProgramsList programs = mDataSource.getPrograms(2, String.valueOf(-1), null);
        if (mAdapter == null) {
            mAdapter = new TVProgramFavoritesFragment.ProgramAdapter(programs);
            mProgramView.setAdapter(mAdapter);
        } else {
            mAdapter.setProgramList(programs);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ProgramHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private TextView mStart;
        private TextView mTitle;
        private TVProgram mProgram;
        private TextView mDuration;
        private TextView mChannelName;
        private ImageView mChannelIcon;
        private ImageView mFavoriteIcon;
        private ImageView mCategoryIcon;
        private ImageView mChannelFavorites;

        public ProgramHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_tvprograms, parent, false));
            itemView.setOnClickListener(this);

            mChannelIcon = itemView.findViewById(R.id.channel_icon);
            mChannelIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProgramChannel();
                }
            });

            mChannelName = itemView.findViewById(R.id.channel_name);
            mChannelName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openProgramChannel();
                }
            });

            mChannelFavorites = itemView.findViewById(R.id.channel_favorites);
            mStart = itemView.findViewById(R.id.text_start);
            mDuration = itemView.findViewById(R.id.text_duration);
            mTitle = itemView.findViewById(R.id.text_title);
            mFavoriteIcon = itemView.findViewById(R.id.image_fav);
            mCategoryIcon = itemView.findViewById(R.id.image_category);

        }

        public void bind(TVProgram program) {
            mProgram = program;

            mChannelName.setText(mProgram.getNameChannel());
            Glide
                    .with(getContext())
                    .load(mDataSource.getChannelIconFile(mProgram.getIndex()))
                    .fitCenter()
                    .into(mChannelIcon);

            if (mProgram.isFavoritChannel()) {
                mChannelFavorites.setImageResource(R.drawable.favorites_on);
            }
            else {
                mChannelFavorites.setImageResource(R.drawable.favorites_off);
            }

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

            mStart.setText(getDateFormat(mProgram.getStart(), "EEE, d MMM HH:mm"));
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

        private void openProgramChannel() {
            Intent intent = TVProgramChannelActivity.newIntent(getActivity(), mProgram.getIndex());
            startActivity(intent);
        }
    }

    private class ProgramAdapter extends RecyclerView.Adapter<ProgramHolder> {
        private TVProgramsList mProgramsList;

        public ProgramAdapter(TVProgramsList programs) {
            mProgramsList = programs;
        }

        @Override
        public ProgramHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ProgramHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ProgramHolder holder, int position) {
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
