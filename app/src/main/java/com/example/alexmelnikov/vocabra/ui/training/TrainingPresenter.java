package com.example.alexmelnikov.vocabra.ui.training;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.utils.CardUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

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
    private int currentCardLevel;
    private int currentCardTimesTrained;
    private HashMap<String, Integer> currentCardOptionsIncrements;


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
        mCardsRep.updateReadyStatusForCardsInDeck(deck);
        currentDeckCards = mCardsRep.getReadyCardsByDeckDB(deck);
        Collections.shuffle(currentDeckCards);
    }

    public void showFrontRequest() {
        getViewState().showFront(currentCard.getFront(), firstAttach);
    }


    public void showBackRequest() {
        getViewState().showBack(currentCard.getBack(), currentCard.getCardContext());
        buttonsLayoutIsExpanded = true;
        getViewState().showOptions(!currentCard.isNew());
    }

    public void optionEasyPicked() {
        getViewState().hideOptions(!currentCard.isNew());
        getViewState().hideCurrentFrontAndBack();
        getNextCard();
    }

    public void optionGoodPicked() {
        getViewState().hideOptions(!currentCard.isNew());
        getViewState().hideCurrentFrontAndBack();
        getNextCard();
    }

    public void optionForgotPicked() {
        getViewState().hideOptions(!currentCard.isNew());
        getViewState().hideCurrentFrontAndBack();
        getNextCard();
    }

    public void optionHardPicked() {
        getViewState().hideOptions(!currentCard.isNew());
        getViewState().hideCurrentFrontAndBack();
        getNextCard();
    }





    private void getNextCard() {
        if (currentDeckCards.size() != 0) {
            currentCardIndex++;
            if (currentCardIndex == currentDeckCards.size())
                currentCardIndex = 0;
            currentCard = currentDeckCards.get(currentCardIndex);
            currentCardLevel = currentCard.getLevel();
            currentCardTimesTrained = currentCard.getTimesTrained();
            currentCardOptionsIncrements = CardUtils.getOptionsIncrementsToDateByLevel(currentCardLevel);
            setupOptionsTextViewsRequest(currentCardOptionsIncrements);

            showFrontRequest();
        }
    }


    private void setupOptionsTextViewsRequest(HashMap<String, Integer> optionsIncrements) {
        String easy, good, hard, forgot;
        easy = optionsIncrements.get("easy") + " д.";
        good = optionsIncrements.get("good") + " д.";
        hard = optionsIncrements.get("hard") + " д.";

        if (currentCardLevel < 3) {
            forgot = "< 1 мин";
            if (currentCardLevel == 1)
                good = "< 10 мин";
        } else {
            forgot = "< 10 мин";
        }


        getViewState().fillOptionsTextViews(!currentCard.isNew(), easy, good, forgot, hard);

    }

}
