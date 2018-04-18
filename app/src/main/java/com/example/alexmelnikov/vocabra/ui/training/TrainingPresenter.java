package com.example.alexmelnikov.vocabra.ui.training;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.data.StatisticsRepository;
import com.example.alexmelnikov.vocabra.data.TranslationsRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.model.temp.TemporaryCard;
import com.example.alexmelnikov.vocabra.utils.CardUtils;

import org.joda.time.DateTime;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;

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
    @Inject
    TranslationsRepository mTransRep;
    @Inject
    StatisticsRepository mStatsRep;

    private Deck currentDeck;
    private int newCardsCount;
    private int oldReadyCardsCount;

    private ArrayList<Card> currentCards;

    private Card currentCard;
    private int currentCardIndex;
    private int currentCardLevel;

    private HashMap<String, Integer> currentCardOptionsIncrements;
    private int easyIncrement;
    private int goodIncrement;
    private int hardIncrement;

    private Deque<TemporaryCard> prevCards;

    private boolean firstAttach;

    private boolean cardBackViewOnScreen;
    private boolean buttonsLayoutIsExpanded;

    public TrainingPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        firstAttach = true;
        buttonsLayoutIsExpanded = false;
        cardBackViewOnScreen = false;
        currentCardIndex = -1;
        prevCards = new ArrayDeque<TemporaryCard>();
        currentCards = new ArrayList<Card>();
    }

    @Override
    public void attachView(TrainingView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

        if (currentCard != null) {
            setupOptionsTextViewsRequest(currentCardOptionsIncrements);
            showFrontRequest();
            getViewState().updatePreviousButton(currentCardIndex > 0);
        } else {
            getNextCard();
        }

//        getNextCard();
        firstAttach = false;



    }

    @Override
    public void detachView(TrainingView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
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
        cardBackViewOnScreen = true;
        getViewState().disableButtonsWhileAnimating();
        getViewState().showOptions(currentCard.getLevel() > 2);
    }

    public void editCardRequest(int methodIndex) {
        getViewState().showEditCardDialog(methodIndex, currentCard, mDecksRep.findDecksByTranslationDirection(currentCard.getTranslationDirection()));
    }


    public void editCardRequest(String front, String back, String cardContext) {
        Card updatedCard = new Card(-1, front, back, currentCard.getFrontLanguage(), currentCard.getBackLanguage(),
                currentDeck, cardContext);
        String initialCardFront = currentCard.getFront();
        String initialCardBack = currentCard.getBack();
        Translation t = mTransRep.findTranslationByCardInDB(currentCard);

        if (mCardsRep.containsSimilarCardInDeckDB(updatedCard, currentDeck)) {
            getViewState().showCardAlreadyExistsMessage(currentDeck.getName());
        } else {
            mCardsRep.updateCardInDB(currentCard, front, back, cardContext, currentDeck);
            if ((!initialCardFront.equals(front) || !initialCardBack.equals(back)) &&
                    t != null) {
                mTransRep.updateTranslationFavoriteStateDB(t, front, back, true, currentCard);
            }
            getViewState().fillEditedCardTextViews(front, back, cardContext);
        }
    }


    public void returnToPreviousCardRequest() {
        if (currentCardIndex > 0) {
            if (buttonsLayoutIsExpanded) {
                getViewState().disableButtonsWhileAnimating();
                buttonsLayoutIsExpanded = false;
                getViewState().hideOptions(currentCard.getLevel() > 2);

            }
            if (currentCardIndex > 0) {
                getViewState().hideCurrentFrontAndBack(true, !cardBackViewOnScreen);
                cardBackViewOnScreen = false;
            }
            getPreviousCard();

            mStatsRep.decreaseTodayCardsTrainedCounter();
        }
    }


    public void optionEasyPicked() {
        buttonsLayoutIsExpanded = false;
        cardBackViewOnScreen = false;
        getViewState().disableButtonsWhileAnimating();
        getViewState().hideOptions(currentCard.getLevel() > 2);
        getViewState().hideCurrentFrontAndBack(false, false);

        Date currentDate = new Date();
        DateTime currentDateTime = new DateTime(currentDate);
        DateTime nextTrainingTime = currentDateTime.plusDays(easyIncrement);

        int nextLevel = currentCardLevel + 2;
        prevCards.push(new TemporaryCard(currentCard));
        mCardsRep.updateCardAfterTraining(currentCard, nextTrainingTime.toDate(), nextLevel, easyIncrement);

        mStatsRep.increaseTodayCardsTrainedCounter();

        updateCounters();
        getNextCard();
    }

    public void optionGoodPicked() {
        buttonsLayoutIsExpanded = false;
        cardBackViewOnScreen = false;
        getViewState().disableButtonsWhileAnimating();
        getViewState().hideOptions(currentCard.getLevel() > 2);
        getViewState().hideCurrentFrontAndBack(false, false);

        Date currentDate = new Date();
        DateTime currentDateTime = new DateTime(currentDate);
        DateTime nextTrainingTime;
        /*if level == 1, do not set new training time, because we want to see the card
          in the current training again*/
        if (currentCardLevel == 1) {
            nextTrainingTime = new DateTime(currentCard.getNextTimeForTraining());
        } else {
            nextTrainingTime = currentDateTime.plusDays(goodIncrement);
        }

        int nextLevel = currentCardLevel + 1;
        prevCards.push(new TemporaryCard(currentCard));
        mCardsRep.updateCardAfterTraining(currentCard, nextTrainingTime.toDate(), nextLevel, goodIncrement);

        mStatsRep.increaseTodayCardsTrainedCounter();

        updateCounters();
        getNextCard();
    }

    public void optionForgotPicked() {
        buttonsLayoutIsExpanded = false;
        cardBackViewOnScreen = false;
        getViewState().disableButtonsWhileAnimating();
        getViewState().hideOptions(currentCard.getLevel() > 2);
        getViewState().hideCurrentFrontAndBack(false, false);

        int nextLevel = 1;
       // currentCards.add(currentCard);
        prevCards.push(new TemporaryCard(currentCard));
        mCardsRep.updateCardAfterTraining(currentCard, currentCard.getNextTimeForTraining(), nextLevel, -1);

        mStatsRep.increaseTodayCardsTrainedCounter();

        updateCounters();
        getNextCard();
    }

    public void optionHardPicked() {
        buttonsLayoutIsExpanded = false;
        cardBackViewOnScreen = false;
        getViewState().disableButtonsWhileAnimating();
        getViewState().hideOptions(currentCard.getLevel() > 2);
        getViewState().hideCurrentFrontAndBack(false, false);

        Date currentDate = new Date();
        DateTime currentDateTime = new DateTime(currentDate);
        DateTime nextTrainingTime = currentDateTime.plusDays(hardIncrement);

        int nextLevel = currentCardLevel - 1;
        prevCards.push(new TemporaryCard(currentCard));
        mCardsRep.updateCardAfterTraining(currentCard, nextTrainingTime.toDate(), nextLevel, hardIncrement);

        mStatsRep.increaseTodayCardsTrainedCounter();

        updateCounters();
        getNextCard();
    }



    private void getNextCard() {
        currentCardIndex++;
        if (currentCardIndex < currentCards.size()) {
            currentCard = currentCards.get(currentCardIndex);
            currentCardLevel = currentCard.getLevel();

            currentCardOptionsIncrements = CardUtils.getOptionsIncrementsToDateByLevel(currentCardLevel);
            easyIncrement = currentCardOptionsIncrements.get("easy");
            goodIncrement = currentCardOptionsIncrements.get("good");
            hardIncrement = currentCardOptionsIncrements.get("hard");
            setupOptionsTextViewsRequest(currentCardOptionsIncrements);

            showFrontRequest();
            updatePreviousButtonEnabledStatus();
        } else {
            ArrayList<Card> oldReadyCards = mCardsRep.getOldReadyForTrainCardsByDeckDB(currentDeck);
            if (oldReadyCards.size() > 0) {
                currentCards.addAll(oldReadyCards);
                currentCardIndex--;
                getNextCard();
            } else {
                getViewState().closeFragment();
            }
        }
    }


    private void getPreviousCard() {
        if (currentCardIndex > 0) {
            currentCardIndex--;
            TemporaryCard lastCard = prevCards.pop();
            currentCard = mCardsRep.getCardByIdDB(lastCard.getId());

            mCardsRep.updateCardAfterReturnUsingOldVirsionOfCard(currentCard, lastCard.isNew(),
                    lastCard.getLastTimeTrained(), lastCard.getNextTimeForTraining(), lastCard.getLevel());

            currentCardLevel = currentCard.getLevel();
            updateCounters();

            currentCardOptionsIncrements = CardUtils.getOptionsIncrementsToDateByLevel(currentCardLevel);
            easyIncrement = currentCardOptionsIncrements.get("easy");
            goodIncrement = currentCardOptionsIncrements.get("good");
            hardIncrement = currentCardOptionsIncrements.get("hard");
            setupOptionsTextViewsRequest(currentCardOptionsIncrements);

            showFrontRequest();
            updatePreviousButtonEnabledStatus();
        }
    }


    private void updatePreviousButtonEnabledStatus() {
        getViewState().updatePreviousButton(currentCardIndex > 0);
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
