package com.example.alexmelnikov.vocabra.ui;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.MvpFragment;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by AlexMelnikov on 03.03.18.
 */

public class BaseFragment extends MvpAppCompatFragment {

    protected CompositeDisposable mDisposable = new CompositeDisposable();

}
