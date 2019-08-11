package com.nkvoronov.tvprogram.tvchannels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tvprogram.TVProgramChannelActivity;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;

public class TVChannelsFragment extends Fragment{
    private static final String ARG_PAGE_NUMBER = "page_number";
    private Spinner mSpinnerFilter;
    private RecyclerView mChannelsView;
    private TextView mEmptyTextView;
    private ChannelAdapter mAdapter;
    private int mPageIndex;
    private ChangesChannels mChangesChannels;
    private TVProgramDataSource mDataSource;
    private int mFilterIndex;

    public interface ChangesChannels {
        void onUpdatePages(TVChannel channel);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mChangesChannels = (ChangesChannels) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mChangesChannels = null;
    }

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
        mFilterIndex = 0;
        Log.d(TAG, "PageIndex : " + mPageIndex);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvchannels, container, false);
        mDataSource = TVProgramDataSource.get(getContext());
        mSpinnerFilter = root.findViewById(R.id.tvchannel_filter);
        mSpinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mFilterIndex != i) {
                    mFilterIndex = i;
                    updateUI();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });
        if (isFavorites()) {
            mSpinnerFilter.setVisibility(View.GONE);
        }
        mChannelsView = root.findViewById(R.id.tvchannel_view);
        mEmptyTextView = root.findViewById(R.id.tvchannel_empty);
        mChannelsView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public boolean isFavorites() {
        return mPageIndex == 0;
    }

    public void updateUI() {
        Log.d(TAG, "UpdateUI " + mPageIndex);
        TVChannelsList channelList = mDataSource.getChannels(isFavorites(), mFilterIndex);

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
        private ImageView mChannelUpdate;
        private ImageView mChannelFavorites;

        public ChannelHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_tvchannels, parent, false));
            itemView.setOnClickListener(this);
            mChannelIcon = itemView.findViewById(R.id.channel_icon);
            mChannelName = itemView.findViewById(R.id.channel_name);
            mChannelUpdate = itemView.findViewById(R.id.channel_update);
            mChannelUpdate.setVisibility(View.GONE);
            mChannelUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //
                }
            });
            mChannelFavorites = itemView.findViewById(R.id.channel_favorites);
            mChannelFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChannel.changeFavorites();
                    mChangesChannels.onUpdatePages(mChannel);
                }
            });
        }

        public void bind(TVChannel channel) {
            mChannel = channel;
            mChannelName.setText(mChannel.getName());
            Glide
                    .with(getContext())
                    .load(Uri.parse(mChannel.getIcon()))
                    .fitCenter()
                    .into(mChannelIcon);

            if (mChannel.isUpdate()) {
                mChannelUpdate.setVisibility(View.VISIBLE);
            }
            if (channel.isFavorites()) {
                mChannelFavorites.setImageResource(R.drawable.favorites_on);
            }
            else {
                mChannelFavorites.setImageResource(R.drawable.favorites_off);
            }
        }

        @Override
        public void onClick(View view) {
            Intent intent = TVProgramChannelActivity.newIntent(getActivity());
            startActivity(intent);
        }
    }

    private class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {
        private TVChannelsList mChannelList;

        public ChannelAdapter(TVChannelsList channelList) {
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

        public void setChannelList(TVChannelsList channelList) {
            mChannelList = channelList;
        }
    }

}
