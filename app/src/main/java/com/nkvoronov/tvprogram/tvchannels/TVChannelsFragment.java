package com.nkvoronov.tvprogram.tvchannels;

import android.util.Log;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Spinner;
import android.content.Context;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.nkvoronov.tvprogram.R;
import android.app.ProgressDialog;
import android.widget.AdapterView;
import android.view.LayoutInflater;
import androidx.fragment.app.Fragment;
import android.content.DialogInterface;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.nkvoronov.tvprogram.tasks.UpdateProgramsTask;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import com.nkvoronov.tvprogram.tvprogram.TVProgramChannelActivity;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.TAG;

public class TVChannelsFragment extends Fragment{
    private static final String ARG_TVCHANNELS_PAGE_NUMBER = "com.nkvoronov.tvprogram.tvchannels.page_number";

    private int mPageIndex;
    private int mFilterIndex;
    private Spinner mSpinnerFilter;
    private TextView mEmptyTextView;
    private ChannelAdapter mAdapter;
    private RecyclerView mChannelsView;
    private ChangesChannels mChangesChannels;
    private TVProgramDataSource mDataSource;

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
        args.putSerializable(ARG_TVCHANNELS_PAGE_NUMBER, index);
        Log.d(TAG, "PageIndex : " + index);

        TVChannelsFragment fragment = new TVChannelsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_TVCHANNELS_PAGE_NUMBER);
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
        TVChannelsList channels = mDataSource.getChannels(isFavorites(), mFilterIndex);

        if (mAdapter == null) {
            mAdapter = new ChannelAdapter(channels);
            mChannelsView.setAdapter(mAdapter);
        } else {
            mAdapter.setChannelList(channels);
            mAdapter.notifyDataSetChanged();
        }
    }

    private class ChannelHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        private TVChannel mChannel;
        private ImageView mChannelIcon;
        private TextView mChannelName;
        private ImageView mChannelFavorites;
        private ProgressDialog mProgressDialog;
        private UpdateProgramsTask mUpdateTask;

        public ChannelHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_tvchannels, parent, false));
            itemView.setOnClickListener(this);
            mChannelIcon = itemView.findViewById(R.id.channel_icon);
            mChannelName = itemView.findViewById(R.id.channel_name);
            mChannelFavorites = itemView.findViewById(R.id.channel_favorites);
            mChannelFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mChannel.changeFavorites();
                    mChangesChannels.onUpdatePages(mChannel);
                }
            });

            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setTitle(getString(R.string.prg_update_caption));

            mUpdateTask = new UpdateProgramsTask(mDataSource);
            mUpdateTask.setListeners(new UpdateProgramsTask.OnTaskListeners() {

                @Override
                public void onStart() {
                    mProgressDialog.setMessage(getString(R.string.program_progress_msg));
                    mProgressDialog.show();
                }

                @Override
                public void onUpdate(String[] progress) {
                    if (progress[0].equals("-1")) {
                        mProgressDialog.setMessage(getActivity().getString(R.string.program_progress_msg1, mChannel.getName(), progress[1]));
                    } else {
                        mProgressDialog.setMessage(getActivity().getString(R.string.program_progress_msg2));
                    }
                    mProgressDialog.setProgress(Integer.parseInt(progress[2]));
                }

                @Override
                public void onStop() {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    openProgramChannel();
                }
            });
        }

        public void bind(TVChannel channel) {
            mChannel = channel;
            mChannelName.setText(mChannel.getName());
            Glide
                    .with(getContext())
                    .load(mChannel.getIconFile())
                    .fitCenter()
                    .into(mChannelIcon);

            if (channel.isFavorites()) {
                mChannelFavorites.setImageResource(R.drawable.favorites_on);
            }
            else {
                mChannelFavorites.setImageResource(R.drawable.favorites_off);
            }
        }

        @Override
        public void onClick(View view) {
            if (mDataSource.checkUpdateProgram(mChannel.getIndex())) {
                updateProgramForChannel();
            } else {
                openProgramChannel();
            }
        }

        private void openProgramChannel() {
            Intent intent = TVProgramChannelActivity.newIntent(getActivity(), mChannel.getIndex());
            startActivity(intent);
        }

        public void updateProgramForChannel() {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getActivity().getString(R.string.prg_update_caption));
            builder.setMessage(getActivity().getString(R.string.program_not_date, mChannel.getName()));
            builder.setPositiveButton(getActivity().getString(R.string.bt_yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mUpdateTask.execute(mChannel.getIndex());
                }
            });
            builder.setNegativeButton(getActivity().getString(R.string.bt_no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), getActivity().getString(R.string.bt_no), Toast.LENGTH_LONG).show();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private class ChannelAdapter extends RecyclerView.Adapter<ChannelHolder> {
        private TVChannelsList mChannelList;

        public ChannelAdapter(TVChannelsList channels) {
            mChannelList = channels;
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

        public void setChannelList(TVChannelsList channels) {
            mChannelList = channels;
        }
    }

}
