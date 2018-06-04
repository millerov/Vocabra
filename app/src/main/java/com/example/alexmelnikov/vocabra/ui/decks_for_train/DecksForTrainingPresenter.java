package com.example.alexmelnikov.vocabra.ui.decks_for_train;

import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.SnackBarActionHandler;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * DecksForTrainingPresenter.java – presenter for DecksForTrainingFragment
 * @author Alexander Melnikov
 */

@InjectViewState
public class DecksForTrainingPresenter extends MvpPresenter<DecksForTrainingView> implements SnackBarActionHandler{

    private static final String TAG = "MyTag";

    @Inject
    DecksRepository mDecksRep;
    @Inject
    CardsRepository mCardsRep;

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
        if (mCardsRep.getReadyCardsByDeckDB(deck).size() != 0)
            getViewState().openTrainingFragment(deck, item, transitionName);
        else
            getViewState().showEmptyDeckSelectedMessage(item);

    }

    public void showStatisticsButtonPressed() {
        getViewState().openStatisticsFragment();
    }

    @Override
    public void onSnackbarEvent(int actionId) {}

    public void setupCounterRequest(int counter) {
        getViewState().setupTextView(counter);
    }


    private void loadDecks() {
        mCardsRep.updateReadyStatusForAllCards();
        mDecksList = mDecksRep.getDecksFromDB();
        getViewState().replaceCardsRecyclerData(mDecksList);
    }


}
