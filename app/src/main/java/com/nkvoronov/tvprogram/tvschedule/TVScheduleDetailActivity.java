package com.nkvoronov.tvprogram.tvschedule;

import java.io.File;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;
import android.widget.TextView;
import android.widget.ImageView;
import com.nkvoronov.tvprogram.R;
import android.graphics.BitmapFactory;
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
        mButtonExecute.setEnabled(mSchedule.getTimeType()>0);

        mScheduleTitle.setText(mSchedule.getTitle());
        mScheduleDate.setText(getDateFormat(mSchedule.getStarting(), "EEE, d MMM"));
        String duration = getDateFormat(mSchedule.getStarting(), "HH:mm") + " - " + getDateFormat(mSchedule.getEnding(), "HH:mm");
        mScheduleDuration.setText(duration);
        mScheduleChannel.setText(Html.fromHtml("<b>" + getString(R.string.lab_channel) + "</b> " + mSchedule.getNameChannel()));

        mScheduleImage.setVisibility(View.GONE);
        mScheduleGenre.setVisibility(View.GONE);
        mScheduleCountry.setVisibility(View.GONE);
        mScheduleYear.setVisibility(View.GONE);
        mScheduleDescription.setVisibility(View.GONE);

        if (mSchedule.getDescription() != null) {

            if (mSchedule.getDescription().getImage() != null && mSchedule.getDescription().getImage().length() > 0) {
                mScheduleImage.setVisibility(View.VISIBLE);
                File file = mDataSource.getDescriptionImageFile(mSchedule.getDescription().getType(), mSchedule.getDescription().getIdCatalog());
                mScheduleImage.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }

            if (mSchedule.getDescription().getGenres() != null && mSchedule.getDescription().getGenres().length() > 0) {
                mScheduleGenre.setVisibility(View.VISIBLE);
                mScheduleGenre.setText(Html.fromHtml("<b>" + getString(R.string.lab_genres) + "</b> " + mSchedule.getDescription().getGenres()));
            }

            if (mSchedule.getDescription().getCountry() != null && mSchedule.getDescription().getCountry().length() > 0) {
                mScheduleCountry.setVisibility(View.VISIBLE);
                mScheduleCountry.setText(Html.fromHtml("<b>" + getString(R.string.lab_country) + "</b> " + mSchedule.getDescription().getCountry()));
            }

            if (mSchedule.getDescription().getYear() != null && mSchedule.getDescription().getYear().length() > 0) {
                mScheduleYear.setVisibility(View.VISIBLE);
                mScheduleYear.setText(Html.fromHtml("<b>" + getString(R.string.lab_year) + "</b> " + mSchedule.getDescription().getYear()));
            }

            String title = "";
            String directors = "";
            String actors = "";
            String rating = "";
            String description = "";

            if (mSchedule.getDescription().getTitle() != null && mSchedule.getDescription().getTitle().length() > 0) {
                title = "<b>" + getString(R.string.lab_title) + "</b> " + mSchedule.getDescription().getTitle() + "<br>";
            }

            if (mSchedule.getDescription().getDirectors() != null && mSchedule.getDescription().getDirectors().length() > 0) {
                directors = "<b>" + getString(R.string.lab_directors) + "</b> " + mSchedule.getDescription().getDirectors() + "<br>";
            }

            if (mSchedule.getDescription().getActors() != null && mSchedule.getDescription().getActors().length() > 0) {
                actors = "<b>" + getString(R.string.lab_actors) + "</b> " + mSchedule.getDescription().getActors() + "<br>";
            }

            if (mSchedule.getDescription().getRating() != null && mSchedule.getDescription().getRating().length() > 0) {
                rating = "<b>" + getString(R.string.lab_rating) + "</b> " + mSchedule.getDescription().getRating() + "<br>";
            }

            description = title + directors + actors + rating;
            if (description.length() > 0) {
                description = description + "<br>";
            }

            if (mSchedule.getDescription().getDescription() != null && mSchedule.getDescription().getDescription().length() > 0) {
                mScheduleDescription.setVisibility(View.VISIBLE);
                mScheduleDescription.setText(Html.fromHtml(description + mSchedule.getDescription().getDescription()));
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
