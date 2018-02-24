package com.example.alexmelnikov.vocabra.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.alexmelnikov.vocabra.R;

/**
 * Created by AlexMelnikov on 24.02.18.
 */

public class WordBrowserFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_word_browser, container, false);
    }
}
