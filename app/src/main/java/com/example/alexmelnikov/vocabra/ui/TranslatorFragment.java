package com.example.alexmelnikov.vocabra.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpFragment;
import com.example.alexmelnikov.vocabra.R;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends MvpFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_translator, container, false);
    }

}

