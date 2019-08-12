package com.nkvoronov.tvprogram.tvchannels;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.material.tabs.TabLayout;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVChannelsActivity extends AppCompatActivity implements TVChannelsFragment.ChangesChannels {
    private TVChannelsPageAdapter mPageAdapter;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    ProgressDialog mProgressDialog;
    private TVProgramDataSource mDataSource;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_tvchannels, menu);
        MenuCompat.setGroupDividerEnabled(menu, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_update:
                onUpdate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onUpdate() {
        new updateChannelsTask().execute();
        onUpdatePages(null);
    }

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, TVChannelsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvchannels);
        mDataSource = TVProgramDataSource.get(this);
        mTabLayout = findViewById(R.id.tvprogramchannels_tabs);
        mViewPager = findViewById(R.id.tvprogramchannels_pager);
        mPageAdapter = new TVChannelsPageAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPageAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mProgressDialog = new ProgressDialog(this);
    }

    @Override
    public void onUpdatePages(TVChannel channel) {
        for (int i = 0; i < mPageAdapter.getCount(); i++) {
            ((TVChannelsFragment) mPageAdapter.instantiateItem(mViewPager, i)).updateUI();
        }
    }

    public class updateChannelsTask extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Doing something, please wait.");
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mDataSource.updateChannels();
            return null;
        }
    }
}
