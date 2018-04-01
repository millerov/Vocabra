package com.example.alexmelnikov.vocabra.ui.decks_for_train;

import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Deck;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 28.03.18.
 */

@InjectViewState
public class DecksForTrainingPresenter extends MvpPresenter<DecksForTrainingView> {

    private static final String TAG = "MyTag";

    @Inject
    DecksRepository mDecksRep;

    private ArrayList<Deck> mDecksList;


    public DecksForTrainingPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
    }

    @Override
    public void attachView(DecksForTrainingView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        setupCounterRequest(0);
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

    public void setupCounterRequest(int counter) {
        getViewState().setupTextView(counter);
    }


    private void loadDecks() {
        mDecksList = mDecksRep.getDecksFromDB();
        getViewState().replaceCardsRecyclerData(mDecksList);
    }
}
