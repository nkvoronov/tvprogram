package com.nkvoronov.tvprogram;

import java.util.List;
import android.os.Bundle;
import android.view.View;
import java.util.ArrayList;
import android.widget.Button;
import android.widget.Spinner;
import android.content.Intent;
import android.widget.CheckBox;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.common.MainDataSource;

public class SettingsActivity extends AppCompatActivity {
    private Button mClearDB;
    private CheckBox mFullDesc;
    private Spinner  mSpinnerCountDays;
    private MainDataSource mDataSource;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    private void setDefCountDays() {
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
        setDefCountDays();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mDataSource = MainDataSource.get(this);
        addSpinnerCountDays();
        mFullDesc = findViewById(R.id.full_desc);
        mFullDesc.setChecked(mDataSource.isFullDesc());
        mFullDesc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mDataSource.setFullDesc(b);
            }
        });
        mClearDB = findViewById(R.id.bt_cleardatadb);
        mClearDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearDB();
            }
        });
    }

    private void clearDB() {
        mDataSource.clearDB();
    }
}
