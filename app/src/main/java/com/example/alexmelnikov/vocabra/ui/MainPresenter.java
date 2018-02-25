package com.example.alexmelnikov.vocabra.ui;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

import ru.terrakok.cicerone.Router;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {
    private Router router;

    public MainPresenter(Router router) {
        this.router = router;
    }
}
