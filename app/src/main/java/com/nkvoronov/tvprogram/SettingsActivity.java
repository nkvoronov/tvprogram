package com.nkvoronov.tvprogram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;
import java.util.ArrayList;
import java.util.List;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.ALL_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.RUS_LANG;
import static com.nkvoronov.tvprogram.common.TVProgramDataSource.UKR_LANG;

public class SettingsActivity extends AppCompatActivity {
    private Spinner  mSpinnerCountDays;
    private Spinner  mSpinnerLangProgram;
    private CheckBox mIndexSort;
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

    private int getPositionForLangProgram() {
        switch (mDataSource.getLang()) {
            case RUS_LANG:
                return 0;
            case UKR_LANG:
                return 1;
            case ALL_LANG:
                return 2;
            default:
                return -1;
        }
    }

    private void setItemLangProgram() {
        mSpinnerLangProgram.setSelection(getPositionForLangProgram());
    }

    private void getItemLangProgram(int position) {
        int new_val = position;
        int old_val = getPositionForLangProgram();
        String lang_val = "";
        if (new_val != old_val) {
            switch (new_val) {
                case 0:
                    mDataSource.setLang(RUS_LANG);
                    lang_val = RUS_LANG;
                    break;
                case 1:
                    lang_val = UKR_LANG;
                    break;
                default:
                    lang_val = ALL_LANG;
                    break;
            }
            mDataSource.setLang(lang_val);
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

    private void addSpinnerLangProgram() {
        mSpinnerLangProgram = findViewById(R.id.spn_lang);
        List<String> list = new ArrayList<String>();
        list.add(this.getString(R.string.lang_rus));
        list.add(this.getString(R.string.lang_ukr));
        list.add(this.getString(R.string.lang_all));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerLangProgram.setAdapter(dataAdapter);
        mSpinnerLangProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                getItemLangProgram(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });
        setItemLangProgram();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDataSource = TVProgramDataSource.get(this);

        addSpinnerCountDays();
        addSpinnerLangProgram();

        mIndexSort = findViewById(R.id.chb_index_sort);
        mIndexSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                  mDataSource.setIndexSort(b);
              }
        });
        mIndexSort.setChecked(mDataSource.isIndexSort());
    }
}
