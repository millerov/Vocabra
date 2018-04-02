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

import org.joda.time.DateTime;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
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
    private int newCardsCount;
    private int oldReadyCardsCount;

    private ArrayList<Card> currentCards;

    private Card currentCard;
    private int currentCardIndex;
    private int currentCardLevel;
    private int currentCardTimesTrained;

    private HashMap<String, Integer> currentCardOptionsIncrements;
    private int easyIncrement;
    private int goodIncrement;
    private int hardIncrement;

    private Deque<Card> prevCards;


    private boolean firstAttach;

    private boolean buttonsLayoutIsExpanded;

    public TrainingPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        firstAttach = true;
        buttonsLayoutIsExpanded = false;
        currentCardIndex = -1;
        prevCards = new ArrayDeque<Card>();
        currentCards = new ArrayList<Card>();
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
        updateCounters();
        mCardsRep.updateReadyStatusForCardsInDeck(deck);

        /*Firstly we want to train only new cards
        then after finished train all other ready for training cards*/
        currentCards.addAll(mCardsRep.getNewCardsByDeckDB(deck));
        currentCards.addAll(mCardsRep.getOldReadyForTrainCardsByDeckDB(deck));

        Collections.shuffle(currentCards);
    }


    public void updateCounters() {
        mCardsRep.updateReadyStatusForCardsInDeck(currentDeck);
        this.newCardsCount = mCardsRep.getNewCardsByDeckDB(currentDeck).size();
        this.oldReadyCardsCount = mCardsRep.getOldReadyForTrainCardsByDeckDB(currentDeck).size();
        getViewState().fillCounters(newCardsCount, oldReadyCardsCount);
    }

    public void showFrontRequest() {
        getViewState().showFront(currentCard.getFront(), firstAttach);
    }


    public void showBackRequest() {
        getViewState().showBack(currentCard.getBack(), currentCard.getCardContext());
        buttonsLayoutIsExpanded = true;
        getViewState().showOptions(currentCard.getLevel() > 2);
    }


    public void returnToPreviousCardRequest() {
        getPreviousCard();
    }


    public void optionEasyPicked() {
        buttonsLayoutIsExpanded = false;
        getViewState().hideOptions(currentCard.getLevel() > 2);
        getViewState().hideCurrentFrontAndBack();

        Date currentDate = new Date();
        DateTime currentDateTime = new DateTime(currentDate);
        DateTime nextTrainingTime = currentDateTime.plusDays(easyIncrement);

        int nextLevel = currentCardLevel + 2;
        mCardsRep.updateCardAfterTraining(currentCard, nextTrainingTime.toDate(), nextLevel);

        updateCounters();
        getNextCard();
    }

    public void optionGoodPicked() {
        buttonsLayoutIsExpanded = false;
        getViewState().hideOptions(currentCard.getLevel() > 2);
        getViewState().hideCurrentFrontAndBack();

        Date currentDate = new Date();
        DateTime currentDateTime = new DateTime(currentDate);
        DateTime nextTrainingTime;
        /*if level == 1, do not set new training time, because we want to see the card
          in the current training again*/
        if (currentCardLevel == 1) {
            nextTrainingTime = new DateTime(currentCard.getNextTimeForTraining());
            currentCards.add(currentCard);
        } else {
            nextTrainingTime = currentDateTime.plusDays(goodIncrement);
        }

        int nextLevel = currentCardLevel + 1;
        mCardsRep.updateCardAfterTraining(currentCard, nextTrainingTime.toDate(), nextLevel);

        updateCounters();
        getNextCard();
    }

    public void optionForgotPicked() {
        buttonsLayoutIsExpanded = false;
        getViewState().hideOptions(currentCard.getLevel() > 2);
        getViewState().hideCurrentFrontAndBack();

        int nextLevel = 1;
        currentCards.add(currentCard);
        mCardsRep.updateCardAfterTraining(currentCard, currentCard.getNextTimeForTraining(), nextLevel);

        updateCounters();
        getNextCard();
    }

    public void optionHardPicked() {
        buttonsLayoutIsExpanded = false;
        getViewState().hideOptions(currentCard.getLevel() > 2);
        getViewState().hideCurrentFrontAndBack();

        Date currentDate = new Date();
        DateTime currentDateTime = new DateTime(currentDate);
        DateTime nextTrainingTime = currentDateTime.plusDays(hardIncrement);

        int nextLevel = currentCardLevel - 1;
        mCardsRep.updateCardAfterTraining(currentCard, nextTrainingTime.toDate(), nextLevel);

        updateCounters();
        getNextCard();
    }



    private void getNextCard() {
        currentCardIndex++;
        if (currentCardIndex < currentCards.size()) {
            currentCard = currentCards.get(currentCardIndex);
            currentCardLevel = currentCard.getLevel();
            currentCardTimesTrained = currentCard.getTimesTrained();

            prevCards.push(currentCard);

            currentCardOptionsIncrements = CardUtils.getOptionsIncrementsToDateByLevel(currentCardLevel);
            easyIncrement = currentCardOptionsIncrements.get("easy");
            goodIncrement = currentCardOptionsIncrements.get("good");
            hardIncrement = currentCardOptionsIncrements.get("hard");
            setupOptionsTextViewsRequest(currentCardOptionsIncrements);

            showFrontRequest();
        } else {
            getViewState().closeFragment();
        }
    }


    private void getPreviousCard() {
       /* if (currentCardIndex > 0) {
            *//*currentCardIndex--;
            currentCard = prevCards.pop();
            mCardsRep.updateCardAfterReturnUsingOldVirsionOfCard(currentCard);*//*
            if (buttonsLayoutIsExpanded)
                getViewState().hideOptions(currentCard.getLevel() > 2);
        }*/
    }


    private void setupOptionsTextViewsRequest(HashMap<String, Integer> optionsIncrements) {
        String easy, good, hard, forgot;
        easy = easyIncrement + " д.";
        good = goodIncrement + " д.";
        hard = hardIncrement + " д.";

        if (currentCardLevel < 3) {
            forgot = "< 1 мин";
            if (currentCardLevel == 1)
                good = "< 10 мин";
        } else {
            forgot = "< 10 мин";
        }

        getViewState().fillOptionsTextViews(currentCard.getLevel() > 2, easy, good, forgot, hard);

    }

}
