package com.nkvoronov.tvprogram.ui;

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
import com.nkvoronov.tvprogram.common.Channel;
import com.nkvoronov.tvprogram.common.ChannelList;

public class PageChannels extends Fragment {
    private static final String ARG_PAGE_NUMBER = "page_number";
    private static final String TAG = "PAGE_CHANNEL";

    private ChannelList mChannelList;
    private RecyclerView mChannelsView;
    private TextView mEmptyTextView;
    private ChannelAdapter mAdapter;
    private int mPageIndex;

    public static PageChannels newInstance(int index) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PAGE_NUMBER, index);
        Log.d(TAG, "PageIndex : " + index);

        PageChannels fragment = new PageChannels();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_PAGE_NUMBER);
        Log.d(TAG, "PageIndex : " + mPageIndex);
        if (mPageIndex == 0) {
            mChannelList = new ChannelList(getContext(), "ru", true);
        } else {
            mChannelList = new ChannelList(getContext(), "ua", false);
        }
        mChannelList.loadFromNet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_channels, container, false);
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

        if (mAdapter == null) {
            mAdapter = new ChannelAdapter(mChannelList);
            mChannelsView.setAdapter(mAdapter);
        } else {
            mAdapter.setChannelList(mChannelList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ChannelHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private Channel mChannel;
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

        public void bind(Channel channel) {
            mChannel = channel;
            mChannelName.setText(mChannel.getOName());
            Glide
                    .with(getContext())
                    .load(Uri.parse(mChannel.getIcon()))
                    .fitCenter()
                    .into(mChannelIcon);
            //mChannelIcon.setImageURI(Uri.parse(mChannel.getIcon()));
        }

        @Override
        public void onClick(View view) {
            //
        }
    }

    private class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {
        private ChannelList mChannelList;

        public ChannelAdapter(ChannelList channelList) {
            mChannelList = channelList;
        }

        @Override
        public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ChannelHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ChannelHolder holder, int position) {
            Channel channel = mChannelList.getChannel(position);
            holder.bind(channel);
        }

        @Override
        public int getItemCount() {
            if (mChannelList.getSize() == 0) {
                mEmptyTextView.setVisibility(View.VISIBLE);
            } else {
                mEmptyTextView.setVisibility(View.INVISIBLE);
            }
            return mChannelList.getSize();
        }

        public void setChannelList(ChannelList channelList) {
            mChannelList = channelList;
        }
    }

}
