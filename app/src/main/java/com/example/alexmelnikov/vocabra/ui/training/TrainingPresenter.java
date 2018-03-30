package com.example.alexmelnikov.vocabra.ui.training;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Deck;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 29.03.18.
 */

@InjectViewState
public class TrainingPresenter extends MvpPresenter<TrainingView> {

    private static final String TAG = "MyTag";

    @Inject
    DecksRepository mDecksRep;

    private Deck currentDeck;
    private boolean firstAttach;

    private boolean buttonsLayoutIsExpanded;

    public TrainingPresenter() {
        firstAttach = true;
        buttonsLayoutIsExpanded = false;
    }

    @Override
    public void attachView(TrainingView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        showFront();
        firstAttach = false;

    }

    @Override
    public void detachView(TrainingView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    public void setupDeck(Deck deck) {
        currentDeck = deck;
    }

    public void showFront() {
        getViewState().showFrontView(firstAttach);
    }



    public void showBackRequest() {
        getViewState().showBackView();
        buttonsLayoutIsExpanded = true;
        getViewState().expandButtonsLayout();
    }

    public void moreButtonPressed() {

    }

}
