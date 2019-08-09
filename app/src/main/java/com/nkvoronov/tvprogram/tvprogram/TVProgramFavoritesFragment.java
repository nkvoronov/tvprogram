package com.nkvoronov.tvprogram.tvprogram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.nkvoronov.tvprogram.R;

public class TVProgramFavoritesFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    public static TVProgramFavoritesFragment newInstance(int index) {
        TVProgramFavoritesFragment fragment = new TVProgramFavoritesFragment();
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
        textView.setText("PAGE_FAVORITES");
        return root;
    }
}
