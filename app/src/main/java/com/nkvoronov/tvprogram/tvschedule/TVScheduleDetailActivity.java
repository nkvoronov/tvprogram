package com.nkvoronov.tvprogram.tvschedule;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import com.nkvoronov.tvprogram.R;
import android.text.method.LinkMovementMethod;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.common.MainDataSource;
import static com.nkvoronov.tvprogram.common.DateUtils.getDateFormat;

public class TVScheduleDetailActivity extends AppCompatActivity {
    private static final String EXTRA_TVSCHEDULE_ID = "com.nkvoronov.tvprogram.tvschedule.tvschedule_id";

    private int mScheduleId;
    private TVSchedule mSchedule;
    private TextView mScheduleDate;
    private Button mButtonExecute;
    private TextView mScheduleYear;
    private TextView mScheduleTitle;
    private TextView mScheduleGenre;
    private ImageView mScheduleImage;
    private TextView mScheduleCountry;
    private TextView mScheduleChannel;
    private TextView mScheduleDuration;
    private TextView mScheduleDescription;
    private ImageView mScheduleCategoryIcon;
    private MainDataSource mDataSource;

    public static Intent newIntent(Context context, int scheduleId) {
        Intent intent = new Intent(context, TVScheduleDetailActivity.class);
        intent.putExtra(EXTRA_TVSCHEDULE_ID, scheduleId);
        return intent;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable(EXTRA_TVSCHEDULE_ID, mScheduleId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvschedule_detail);
        mDataSource = MainDataSource.get(this);
        if (savedInstanceState != null) {
            mScheduleId = (int) savedInstanceState.getSerializable(EXTRA_TVSCHEDULE_ID);
        } else {
            mScheduleId = (int) getIntent().getSerializableExtra(EXTRA_TVSCHEDULE_ID);
        }
        mSchedule = mDataSource.getSchedule(mScheduleId);
        mScheduleTitle = findViewById(R.id.tvschedule_title);

        mScheduleCategoryIcon = findViewById(R.id.tvschedule_image_category);
        if (mSchedule.getCategory() != 0) {
            mScheduleCategoryIcon.setVisibility(View.VISIBLE);
            mDataSource.setCategoryDrawable(mScheduleCategoryIcon, mSchedule.getCategory());
        } else {
            mScheduleCategoryIcon.setVisibility(View.GONE);
        }

        mScheduleImage = findViewById(R.id.tvschedule_image_desc);
        mScheduleImage.setImageResource(R.drawable.rectangle_control);
        mScheduleDate = findViewById(R.id.tvschedule_date);
        mScheduleDuration = findViewById(R.id.tvschedule_duration);
        mScheduleChannel = findViewById(R.id.tvschedule_channel);
        mScheduleGenre = findViewById(R.id.tvschedule_genre);
        mScheduleCountry = findViewById(R.id.tvschedule_country);
        mScheduleYear = findViewById(R.id.tvschedule_year);
        mScheduleDescription = findViewById(R.id.tvschedule_description);
        mScheduleDescription.setMovementMethod(LinkMovementMethod.getInstance());

        mButtonExecute = findViewById(R.id.tvschedule_action);
        mButtonExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSchedule.changeFavorites();
                updateUI();
            }
        });
        updateUI();

        mScheduleTitle.setText(mSchedule.getTitle());
        mScheduleDate.setText(getDateFormat(mSchedule.getStarting(), "EEE, d MMM"));
        String duration = getDateFormat(mSchedule.getStarting(), "HH:mm") + " - " + getDateFormat(mSchedule.getEnding(), "HH:mm");
        mScheduleDuration.setText(duration);
        mScheduleChannel.setText(getString(R.string.lab_channel, mSchedule.getNameChannel()));

        mScheduleGenre.setVisibility(View.GONE);
        mScheduleCountry.setVisibility(View.GONE);
        mScheduleYear.setVisibility(View.GONE);
        mScheduleDescription.setVisibility(View.GONE);

        if (mSchedule.getDescription() != null) {

            if (mSchedule.getDescription().getGenres() != null) {
                mScheduleGenre.setVisibility(View.VISIBLE);
                mScheduleGenre.setText(getString(R.string.lab_genre, mSchedule.getDescription().getGenres()));
            }

            if (mSchedule.getDescription().getCountry() != null) {
                mScheduleCountry.setVisibility(View.VISIBLE);
                mScheduleCountry.setText(getString(R.string.lab_country, mSchedule.getDescription().getCountry()));
            }

            if (mSchedule.getDescription().getYear() != null) {
                mScheduleYear.setVisibility(View.VISIBLE);
                mScheduleYear.setText(getString(R.string.lab_year, mSchedule.getDescription().getYear()));
            }

            if (mSchedule.getDescription().getDescription() != null) {
                mScheduleDescription.setVisibility(View.VISIBLE);
                mScheduleDescription.setText(Html.fromHtml(mSchedule.getDescription().getDescription()));
            }
        }

    }

    public void updateUI() {
        if (mSchedule.isFavorites()) {
            mButtonExecute.setText(R.string.del_from_favorites);
        } else {
            mButtonExecute.setText(R.string.add_to_favorites);
        }
    }
}
