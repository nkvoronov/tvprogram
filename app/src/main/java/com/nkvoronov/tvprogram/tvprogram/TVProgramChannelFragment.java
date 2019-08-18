package com.nkvoronov.tvprogram.tvprogram;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import java.util.Date;
import static com.nkvoronov.tvprogram.common.DateUtils.getFormatDate;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;

public class TVProgramChannelFragment  extends Fragment {
    private static final String ARG_TVPROGRAM_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvprogram.page_number";
    private static final String ARG_TVPROGRAM_CHANNEL_INDEX = "com.nkvoronov.tvprogram.tvprogram.channel_index";
    private static final String ARG_TVPROGRAM_DATE = "com.nkvoronov.tvprogram.tvprogram.program_date";
    private RecyclerView mProgramView;
    private TextView mEmptyTextView;
    private ProgramChannelAdapter mAdapter;
    private int mPageIndex;
    private int mChannelIndex;
    private Date mProgramDate;
    private TVProgramDataSource mDataSource;

    public static TVProgramChannelFragment newInstance(int page, int channel, Date date) {
        TVProgramChannelFragment fragment = new TVProgramChannelFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TVPROGRAM_PAGE_NUMBER, page);
        args.putSerializable(ARG_TVPROGRAM_CHANNEL_INDEX, channel);
        args.putSerializable(ARG_TVPROGRAM_DATE, date.getTime());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_TVPROGRAM_PAGE_NUMBER);
        mChannelIndex = (int) getArguments().getSerializable(ARG_TVPROGRAM_CHANNEL_INDEX);
        long dt = (long) getArguments().getSerializable(ARG_TVPROGRAM_DATE);
        mProgramDate = new Date(dt);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvprogram_channel, container, false);
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
        Log.d(TAG, "UpdateUI " + mPageIndex);
        TVProgramsList programs = mDataSource.getPrograms(0, mChannelIndex, null);

        if (mAdapter == null) {
            mAdapter = new ProgramChannelAdapter(programs);
            mProgramView.setAdapter(mAdapter);
        } else {
            mAdapter.setProgramList(programs);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ProgramChannelHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private TVProgram mProgram;
        private TextView mStart;
        private TextView mEnd;
        private TextView mTitle;

        public ProgramChannelHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_tvprogram, parent, false));
            itemView.setOnClickListener(this);
            mStart = itemView.findViewById(R.id.text_start);
            mEnd = itemView.findViewById(R.id.text_end);
            mTitle = itemView.findViewById(R.id.text_title);
        }

        public void bind(TVProgram program) {
            mProgram = program;
            mStart.setText(getFormatDate(mProgram.getStart(),"yyyy-MM-dd HH:mm"));
            mEnd.setText(getFormatDate(mProgram.getStop(),"yyyy-MM-dd HH:mm"));
            mTitle.setText(mProgram.getTitle());
        }

        @Override
        public void onClick(View view) {
            //
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
