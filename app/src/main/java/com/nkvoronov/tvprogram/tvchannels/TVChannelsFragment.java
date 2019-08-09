package com.nkvoronov.tvprogram.tvchannels;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import java.util.List;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;

public class TVChannelsFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "page_number";

    private RecyclerView mChannelsView;
    private TextView mEmptyTextView;
    private ChannelAdapter mAdapter;
    private int mPageIndex;

    private TVProgramDataSource mDataSource;

    public static TVChannelsFragment newInstance(int index) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PAGE_NUMBER, index);
        Log.d(TAG, "PageIndex : " + index);

        TVChannelsFragment fragment = new TVChannelsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_PAGE_NUMBER);
        Log.d(TAG, "PageIndex : " + mPageIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvchannels, container, false);

        mDataSource = TVProgramDataSource.get(getContext());

        mChannelsView = root.findViewById(R.id.channel_view);
        mEmptyTextView = root.findViewById(R.id.empty_label);
        mChannelsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        Log.d(TAG, "UpdateUI " + mPageIndex);

        List<TVChannel> channelList = mDataSource.getChannels(mPageIndex == 1);

        if (mAdapter == null) {
            mAdapter = new ChannelAdapter(channelList);
            mChannelsView.setAdapter(mAdapter);
        } else {
            mAdapter.setChannelList(channelList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ChannelHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private TVChannel mChannel;
        private ImageView mChannelIcon;
        private TextView mChannelName;
        private ImageView mChannelStatus;

        public ChannelHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_channels, parent, false));
            itemView.setOnClickListener(this);
            mChannelIcon = itemView.findViewById(R.id.channel_icon);
            mChannelName = itemView.findViewById(R.id.channel_name);
            mChannelStatus = itemView.findViewById(R.id.channel_status);
        }

        public void bind(TVChannel channel) {
            mChannel = channel;
            mChannelName.setText(mChannel.getOName());
            Glide
                    .with(getContext())
                    .load(Uri.parse(mChannel.getIcon()))
                    .fitCenter()
                    .into(mChannelIcon);
            //mChannelIcon.setImageURI(Uri.parse(mChannel.getIcon()));
            if (channel.isFav()) {
                mChannelStatus.setImageResource(R.drawable.favorites_on);
            }
            else {
                mChannelStatus.setImageResource(R.drawable.favorites_off);
            }
        }

        @Override
        public void onClick(View view) {
            //
        }
    }

    private class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {
        private List<TVChannel> mChannelList;

        public ChannelAdapter(List<TVChannel> channelList) {
            mChannelList = channelList;
        }

        @Override
        public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ChannelHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ChannelHolder holder, int position) {
            TVChannel channel = mChannelList.get(position);
            holder.bind(channel);
        }

        @Override
        public int getItemCount() {
            if (mChannelList.size() == 0) {
                mEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyTextView.setVisibility(View.INVISIBLE);
            }
            return mChannelList.size();
        }

        public void setChannelList(List<TVChannel> channelList) {
            mChannelList = channelList;
        }
    }

}