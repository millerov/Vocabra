package com.example.alexmelnikov.vocabra.ui;

import android.support.design.widget.BottomNavigationView;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.Screens;

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

    public void onTabWordBrowserClick() {
        router.replaceScreen(Screens.WORD_BROWSER_SCREEN);
    }

    public void onTabTranslatorClick() {
        router.replaceScreen(Screens.TRANSLATOR_SCREEN);
    }

    public void onBackPressed() {
        router.exit();
    }
}
