package com.example.alexmelnikov.vocabra.ui.training;

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

    @Inject
    DecksRepository mDecksRep;

    private Deck currentDeck;

    public TrainingPresenter() {
    }

    @Override
    public void attachView(TrainingView view) {
        super.attachView(view);
        showFront();

    }

    @Override
    public void detachView(TrainingView view) {
        super.detachView(view);
    }

    public void setupDeck(Deck deck) {
        currentDeck = deck;
    }

    public void showFront() {
        getViewState().showFrontView();
    }

}
