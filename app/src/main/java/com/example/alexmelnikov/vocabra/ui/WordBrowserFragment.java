package com.example.alexmelnikov.vocabra.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpFragment;
import com.example.alexmelnikov.vocabra.R;

import ru.terrakok.cicerone.Navigator;


/**
 * Created by AlexMelnikov on 24.02.18.
 */

public class WordBrowserFragment extends MvpFragment {
    private static final String EXTRA_NAME = "tcf_extra_name";

    private Navigator navigator;

    public static WordBrowserFragment getNewInstance(String name) {
        WordBrowserFragment fragment = new WordBrowserFragment();

        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_NAME, name);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_word_browser, container, false);
    }
}
