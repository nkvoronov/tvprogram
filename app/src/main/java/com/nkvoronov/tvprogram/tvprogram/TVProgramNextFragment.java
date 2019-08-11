package com.nkvoronov.tvprogram.tvprogram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.nkvoronov.tvprogram.R;
import com.nkvoronov.tvprogram.common.TVProgramDataSource;

public class TVProgramNextFragment extends Fragment {
    private static final String ARG_PAGE_NUMBER = "page_number";
    private Spinner mSpinnerFilter;
    private RecyclerView mProgramView;
    private TextView mEmptyTextView;
    private int mPageIndex;
    private TVProgramDataSource mDataSource;

    public static TVProgramNextFragment newInstance(int index) {
        TVProgramNextFragment fragment = new TVProgramNextFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PAGE_NUMBER, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageIndex = (int) getArguments().getSerializable(ARG_PAGE_NUMBER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.page_tvprogram, container, false);
        mDataSource = TVProgramDataSource.get(getContext());
        mSpinnerFilter = root.findViewById(R.id.tvprogram_filter);
        mSpinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //
            }
        });
        mSpinnerFilter.setVisibility(View.GONE);
        mProgramView = root.findViewById(R.id.tvprogram_view);
        mEmptyTextView = root.findViewById(R.id.tvprogram_empty);
        mProgramView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        //
    }
}
