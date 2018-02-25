package com.example.alexmelnikov.vocabra.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.arellomobile.mvp.MvpFragment;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.Screens;
import com.example.alexmelnikov.vocabra.common.BackButtonListener;
import com.example.alexmelnikov.vocabra.common.RouterProvider;
import com.example.alexmelnikov.vocabra.navigation.LocalCiceroneHolder;

import javax.inject.Inject;

import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.android.SupportAppNavigator;
import ru.terrakok.cicerone.commands.Back;
import ru.terrakok.cicerone.commands.Command;
import ru.terrakok.cicerone.commands.Replace;
import ru.terrakok.cicerone.commands.SystemMessage;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

public class TranslatorFragment extends MvpFragment implements BackButtonListener {
    private static final String EXTRA_NAME = "tcf_extra_name";


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

