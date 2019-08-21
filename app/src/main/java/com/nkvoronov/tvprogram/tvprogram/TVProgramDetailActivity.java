package com.nkvoronov.tvprogram.tvprogram;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import com.nkvoronov.tvprogram.R;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.tvchannels.TVChannel;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import static com.nkvoronov.tvprogram.common.DateUtils.getDateFormat;

public class TVProgramDetailActivity extends AppCompatActivity {
    private static final String EXTRA_TVPROGRAM_ID = "com.nkvoronov.tvprogram.tvprogram.tvprogram_id";
    private static final String EXTRA_TVCHANNEL_INDEX = "com.nkvoronov.tvprogram.tvprogram.tvchannel_index_det";
    private TVProgramDataSource mDataSource;
    private int mProgramId;
    private int mChannelIndex;
    private TVProgram mProgram;
    private TVChannel mChannel;
    private TextView mProgramTitle;
    private ImageView mProgramCategoryIcon;
    private ImageView mProgramImage;
    private TextView mProgramDate;
    private TextView mProgramDuration;
    private TextView mProgramChannel;
    private TextView mProgramGenre;
    private TextView mProgramCountry;
    private TextView mProgramYear;
    private TextView mProgramDescription;
    private Button mButtonExecute;

    public static Intent newIntent(Context context, int program_id, int channel_index) {
        Intent intent = new Intent(context, TVProgramDetailActivity.class);
        intent.putExtra(EXTRA_TVPROGRAM_ID, program_id);
        intent.putExtra(EXTRA_TVCHANNEL_INDEX, channel_index);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(EXTRA_TVPROGRAM_ID, mProgramId);
        savedInstanceState.putSerializable(EXTRA_TVCHANNEL_INDEX, mChannelIndex);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvprogram_detail);
        mDataSource = TVProgramDataSource.get(this);
        if (savedInstanceState != null) {
            mProgramId = (int) savedInstanceState.getSerializable(EXTRA_TVPROGRAM_ID);
            mChannelIndex = (int) savedInstanceState.getSerializable(EXTRA_TVCHANNEL_INDEX);
        } else {
            mProgramId = (int) getIntent().getSerializableExtra(EXTRA_TVPROGRAM_ID);
            mChannelIndex = (int) getIntent().getSerializableExtra(EXTRA_TVCHANNEL_INDEX);
        }
        mProgram = mDataSource.getProgram(mChannelIndex, mProgramId);
        mChannel = mDataSource.getChannel(mChannelIndex);
        mProgramTitle = findViewById(R.id.tvprogram_title);
        mProgramCategoryIcon = findViewById(R.id.tvprogram_image_category);
        mProgramImage = findViewById(R.id.tvprogram_image_desc);
        mProgramDate = findViewById(R.id.tvprogram_date);
        mProgramDuration = findViewById(R.id.tvprogram_duration);
        mProgramChannel = findViewById(R.id.tvprogram_channel);
        mProgramGenre = findViewById(R.id.tvprogram_genre);
        mProgramCountry = findViewById(R.id.tvprogram_country);
        mProgramYear = findViewById(R.id.tvprogram_year);
        mProgramDescription = findViewById(R.id.tvprogram_description);
        mButtonExecute = findViewById(R.id.tvprogram_action);

        mProgramTitle.setText(mProgram.getTitle());
        mProgramDate.setText(getDateFormat(mProgram.getStart(), "EEE, d MMM"));
        String duration = getDateFormat(mProgram.getStart(), "HH:mm") + " - " + getDateFormat(mProgram.getStop(), "HH:mm");
        mProgramDuration.setText(duration);
        mProgramChannel.setText(getString(R.string.lab_channel, mChannel.getName()));

        if (mProgram.getGenres() != null) {
            mProgramGenre.setText(getString(R.string.lab_genre, mProgram.getGenres()));
        } else {
            mProgramGenre.setVisibility(View.GONE);
        }
        if (mProgram.getCountry() != null) {
            mProgramCountry.setText(getString(R.string.lab_country, mProgram.getCountry()));
        } else {
            mProgramCountry.setVisibility(View.GONE);
        }
        if (mProgram.getYear() != null) {
            mProgramYear.setText(getString(R.string.lab_year, mProgram.getYear()));
        } else {
            mProgramYear.setVisibility(View.GONE);
        }
        if (mProgram.getDescription() != null) {
            mProgramDescription.setText(mProgram.getDescription());
        } else {
            mProgramDescription.setVisibility(View.GONE);
        }

    }
}
