package com.example.alexmelnikov.vocabra.ui.decks_for_train;

import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Deck;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 28.03.18.
 */

@InjectViewState
public class DecksForTrainingPresenter extends MvpPresenter<DecksForTrainingView> {

    private static final String TAG = "MyTag";

    @Inject
    DecksRepository mDecksRep;


    public DecksForTrainingPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
    }

    @Override
    public void attachView(DecksForTrainingView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        loadDecks();
    }

    @Override
    public void detachView(DecksForTrainingView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    public void deckSelectRequestFromRecycler(Deck deck, View item, String transitionName) {
        getViewState().openTrainingActivity(deck, item, transitionName);
    }





    private void loadDecks() {
        getViewState().replaceCardsRecyclerData(mDecksRep.getDecksFromDB());
    }
}
