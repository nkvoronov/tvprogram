package com.nkvoronov.tvprogram.tvprogram;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.graphics.Color;
import android.widget.EditText;
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

public class TVProgramSearchFragment extends Fragment {
    private static final String ARG_SEARCH_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvprogram.page_number_search";

    private int mPageIndex;
    private ProgramAdapter mAdapter;
    private TextView mEmptyTextView;
    private EditText mSearchEditText;
    private RecyclerView mProgramView;
    private TVProgramDataSource mDataSource;
    private String mSearchString = "";

    public static TVProgramSearchFragment newInstance(int index) {
        TVProgramSearchFragment fragment = new TVProgramSearchFragment();
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
        View root = inflater.inflate(R.layout.page_tvprograms_search, container, false);
        mDataSource = TVProgramDataSource.get(getContext());
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
        mProgramView = root.findViewById(R.id.tvprogram_view);
        mEmptyTextView = root.findViewById(R.id.tvprogram_empty_search);
        mProgramView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
        TVProgramsList programs = mDataSource.getPrograms(3, mSearchString, null);
        if (mAdapter == null) {
            mAdapter = new TVProgramSearchFragment.ProgramAdapter(programs);
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
