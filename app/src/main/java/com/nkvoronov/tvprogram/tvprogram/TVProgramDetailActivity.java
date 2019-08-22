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
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import static com.nkvoronov.tvprogram.common.DateUtils.getDateFormat;

public class TVProgramDetailActivity extends AppCompatActivity {
    private static final String EXTRA_TVPROGRAM_ID = "com.nkvoronov.tvprogram.tvprogram.tvprogram_id";
    private static final String EXTRA_TVCHANNEL_INDEX = "com.nkvoronov.tvprogram.tvprogram.tvchannel_index_det";

    private int mProgramId;
    private int mChannelIndex;
    private TVProgram mProgram;
    private TextView mProgramDate;
    private Button mButtonExecute;
    private TextView mProgramYear;
    private TextView mProgramTitle;
    private TextView mProgramGenre;
    private ImageView mProgramImage;
    private TextView mProgramCountry;
    private TextView mProgramChannel;
    private TextView mProgramDuration;
    private TextView mProgramDescription;
    private ImageView mProgramCategoryIcon;
    private TVProgramDataSource mDataSource;

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
        mProgramTitle = findViewById(R.id.tvprogram_title);

        mProgramCategoryIcon = findViewById(R.id.tvprogram_image_category);
        if (mProgram.getCategory() != 0) {
            mProgramCategoryIcon.setVisibility(View.VISIBLE);
            mDataSource.setCategoryDrawable(mProgramCategoryIcon, mProgram.getCategory());
        } else {
            mProgramCategoryIcon.setVisibility(View.GONE);
        }

        mProgramImage = findViewById(R.id.tvprogram_image_desc);
        mProgramImage.setImageResource(R.drawable.rectangle_control);
        mProgramDate = findViewById(R.id.tvprogram_date);
        mProgramDuration = findViewById(R.id.tvprogram_duration);
        mProgramChannel = findViewById(R.id.tvprogram_channel);
        mProgramGenre = findViewById(R.id.tvprogram_genre);
        mProgramCountry = findViewById(R.id.tvprogram_country);
        mProgramYear = findViewById(R.id.tvprogram_year);
        mProgramDescription = findViewById(R.id.tvprogram_description);

        mButtonExecute = findViewById(R.id.tvprogram_action);
        mButtonExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgram.changeFavorites();
                updateUI();
            }
        });
        updateUI();

        mProgramTitle.setText(mProgram.getTitle());
        mProgramDate.setText(getDateFormat(mProgram.getStart(), "EEE, d MMM"));
        String duration = getDateFormat(mProgram.getStart(), "HH:mm") + " - " + getDateFormat(mProgram.getStop(), "HH:mm");
        mProgramDuration.setText(duration);
        mProgramChannel.setText(getString(R.string.lab_channel, mProgram.getNameChannel()));

        mProgramGenre.setVisibility(View.GONE);
        mProgramCountry.setVisibility(View.GONE);
        mProgramYear.setVisibility(View.GONE);
        mProgramDescription.setVisibility(View.GONE);

        if (mProgram.getDescription() != null) {

            if (mProgram.getDescription().getGenres() != null) {
                mProgramGenre.setVisibility(View.VISIBLE);
                mProgramGenre.setText(getString(R.string.lab_genre, mProgram.getDescription().getGenres()));
            }

            if (mProgram.getDescription().getCountry() != null) {
                mProgramCountry.setVisibility(View.VISIBLE);
                mProgramCountry.setText(getString(R.string.lab_country, mProgram.getDescription().getCountry()));
            }

            if (mProgram.getDescription().getYear() != null) {
                mProgramYear.setVisibility(View.VISIBLE);
                mProgramYear.setText(getString(R.string.lab_year, mProgram.getDescription().getYear()));
            }

            if (mProgram.getDescription().getShortDescription() != null) {
                mProgramDescription.setVisibility(View.VISIBLE);
                mProgramDescription.setText(mProgram.getDescription().getShortDescription());
            }

            if (mProgram.getDescription().getDescription() != null) {
                mProgramDescription.setVisibility(View.VISIBLE);
                mProgramDescription.setText(mProgram.getDescription().getDescription());
            }
        }

    }

    public void updateUI() {
        if (mProgram.isFavorites()) {
            mButtonExecute.setText(R.string.del_from_favorites);
        } else {
            mButtonExecute.setText(R.string.add_to_favorites);
        }
    }
}
