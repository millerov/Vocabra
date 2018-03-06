package com.example.alexmelnikov.vocabra.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.alexmelnikov.vocabra.R;

/**
 * Created by AlexMelnikov on 06.03.18.
 */

public class TranslationFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_translation, container, false);
    }

}
