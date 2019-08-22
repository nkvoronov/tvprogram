package com.nkvoronov.tvprogram.tvprogram;

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
import static com.nkvoronov.tvprogram.common.DateUtils.*;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVProgramNowFragment extends Fragment {
    private static final String ARG_NOW_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvprogram.page_number_now";
    private int mPageIndex;
    private int mCategoryID;
    private Spinner mSpinnerFilter;
    private ProgramAdapter mAdapter;
    private TextView mEmptyTextView;
    private RecyclerView mProgramView;
    private TVProgramDataSource mDataSource;

    public static TVProgramNowFragment newInstance(int index) {
        TVProgramNowFragment fragment = new TVProgramNowFragment();
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
        mSpinnerFilter = view.findViewById(R.id.tvprogram_filter);
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
        View root = inflater.inflate(R.layout.page_tvprograms, container, false);
        mDataSource = TVProgramDataSource.get(getContext());
        addSpinnerFilter(root);
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
        TVProgramsList programs = mDataSource.getPrograms(1, String.valueOf(mCategoryID), null);
        if (mAdapter == null) {
            mAdapter = new TVProgramNowFragment.ProgramAdapter(programs);
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
            mChannelFavorites.setVisibility(View.GONE);

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

            if (mProgram.isFavorites()) {
                mFavoriteIcon.setVisibility(View.VISIBLE);
            } else {
                mFavoriteIcon.setVisibility(View.GONE);
            }

            mTitle.setText(mProgram.getTitle());
            mTitle.setTypeface(null, Typeface.BOLD);
            mTitle.setTextColor(Color.BLACK);

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
