package com.example.alexmelnikov.vocabra.ui.training;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 29.03.18.
 */

@InjectViewState
public class TrainingPresenter extends MvpPresenter<TrainingView> {

    private static final String TAG = "MyTag";

    @Inject
    DecksRepository mDecksRep;
    @Inject
    CardsRepository mCardsRep;

    private Deck currentDeck;
    private ArrayList<Card> currentDeckCards;
    private Card currentCard;
    private int currentCardIndex;

    private boolean firstAttach;

    private boolean buttonsLayoutIsExpanded;

    public TrainingPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        firstAttach = true;
        buttonsLayoutIsExpanded = false;
        currentCardIndex = -1;
    }

    @Override
    public void attachView(TrainingView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        getNextCard();
        firstAttach = false;

    }

    @Override
    public void detachView(TrainingView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
        currentCardIndex = -1;
    }

    public void setupDeck(Deck deck) {
        currentDeck = deck;
        Log.d(TAG, "setupDeck: " + deck.getName());
        currentDeckCards = mCardsRep.getCardsByDeckDB(deck);
        Collections.shuffle(currentDeckCards);
    }

    public void showFrontRequest() {
        getViewState().showFront(currentCard.getFront(), firstAttach);
    }


    public void showBackRequest() {
        getViewState().showBack(currentCard.getBack());
        buttonsLayoutIsExpanded = true;
        getViewState().showOptions(true);
    }



    public void optionEasyPicked() {
        getViewState().hideOptions(true);
        getViewState().hideCurrentFrontAndBack();
        getNextCard();
    }

    public void optionGoodPicked() {
        getViewState().hideOptions(currentCard.isNew());
        getViewState().hideCurrentFrontAndBack();
        getNextCard();
    }

    public void optionForgotPicked() {
        getViewState().hideOptions(currentCard.isNew());
        getViewState().hideCurrentFrontAndBack();
        getNextCard();
    }



    public void moreButtonPressed() {

    }



    private void getNextCard() {
        currentCardIndex++;
        if (currentCardIndex < currentDeckCards.size()) {
            currentCard = currentDeckCards.get(currentCardIndex);
            showFrontRequest();
        } else {
            currentCardIndex = 1;
            currentCard = currentDeckCards.get(currentCardIndex);
            showFrontRequest();
        }
    }

    private void displayNewCardRequest() {

    }
}
