package com.nkvoronov.tvprogram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private Spinner  mSpinnerCountDays;
    private TVProgramDataSource mDataSource;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    private void setItemCountDays() {
        mSpinnerCountDays.setSelection(mDataSource.getCoutDays() - 1);
    }

    private void getItemCountDays(int position) {
        int new_val = position + 1;
        int old_val = mDataSource.getCoutDays();
        if (new_val != old_val) {
            mDataSource.setCoutDays(new_val);
        }
    }

    private void addSpinnerCountDays() {
        mSpinnerCountDays = findViewById(R.id.spn_count_day);
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 7; i++) {
          list.add(String.valueOf(i + 1));
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCountDays.setAdapter(dataAdapter);
        mSpinnerCountDays.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getItemCountDays(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });
        setItemCountDays();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDataSource = TVProgramDataSource.get(this);

        addSpinnerCountDays();
    }
}
