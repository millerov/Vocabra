package com.example.alexmelnikov.vocabra.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpFragment;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.common.BackButtonListener;
import com.example.alexmelnikov.vocabra.common.RouterProvider;

import ru.terrakok.cicerone.Navigator;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends MvpFragment implements BackButtonListener {
    private static final String EXTRA_NAME = "tcf_extra_name";

    private Navigator navigator;

    public static TranslatorFragment getNewInstance(String name) {
        TranslatorFragment fragment = new TranslatorFragment();

        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_NAME, name);
        fragment.setArguments(arguments);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_translator, container, false);
    }

    @Override
    public boolean onBackPressed() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment != null
                && fragment instanceof BackButtonListener
                && ((BackButtonListener) fragment).onBackPressed()) {
            return true;
        } else {
            ((RouterProvider) getActivity()).getRouter().exit();
            return true;
        }
    }
}
