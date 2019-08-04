package com.nkvoronov.tvprogram.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.nkvoronov.tvprogram.R;

public class PageNowProgram extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static PageNowProgram newInstance(int index) {
        PageNowProgram fragment = new PageNowProgram();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        final TextView textView = root.findViewById(R.id.text_view);
        textView.setText("PAGE_NOW");
        return root;
    }
}
