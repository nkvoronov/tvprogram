package com.nkvoronov.tvprogram;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class SettingsActivity extends AppCompatActivity {
    private EditText mCountDays;
    private EditText mLangProgram;
    private CheckBox mIndexSort;
    private TVProgramDataSource mDataSource;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDataSource = TVProgramDataSource.get(this);

        mCountDays = findViewById(R.id.edt_countday);
        mCountDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mDataSource.setCoutDays(Integer.getInteger(charSequence.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });
        mCountDays.setText(mDataSource.getCoutDays());

        mLangProgram = findViewById(R.id.edt_lang);
        mLangProgram.addTextChangedListener(new TextWatcher() {


            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mDataSource.setLang(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //
            }
        });
        mLangProgram.setText(mDataSource.getLang());

        mIndexSort = findViewById(R.id.chb_indexsort);
        mIndexSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                  mDataSource.setIndexSort(b);
              }
        });
        mIndexSort.setChecked(mDataSource.isIndexSort());
    }
}
