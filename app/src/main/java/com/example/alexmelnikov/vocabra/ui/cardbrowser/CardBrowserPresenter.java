package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.TranslationsRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.CardSortMethod;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.model.temp.TemporaryCard;
import com.example.alexmelnikov.vocabra.ui.SnackBarActionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * CardBrowserPresenter.java – presenter for CardBrowserFragment
 * @author Alexander Melnikov
 */

@InjectViewState
public class CardBrowserPresenter extends MvpPresenter<CardBrowserView> implements SnackBarActionHandler {

    private static final String TAG = "MyTag";

    @Inject
    DecksRepository mDecksRep;
    @Inject
    LanguagesRepository mLangRep;
    @Inject
    CardsRepository mCardsRep;
    @Inject
    UserDataRepository mUserData;
    @Inject
    TranslationsRepository mTransRep;

    static int selectedColor;

    private ArrayList<Card> mCardsList;
    private boolean showingDeckCards;
    private Deck currentDeckChoosen;

    private boolean editDeckMode;

    private int mCardSortSelectionIndex;
    private ArrayList<CardSortMethod> mCardSortMethods;
    private CardSortMethod mSelectedSortMethod;

    private boolean selectItemsForDeletionMode;
    private ArrayList<TemporaryCard> temporaryCards;
    private ArrayList<Translation> temporaryCardTranslations;

    private boolean noDecksInDB;

    public CardBrowserPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        showingDeckCards = false;
        editDeckMode = false;
        mCardsList = new ArrayList<>();
        temporaryCards = new ArrayList<>();
        temporaryCardTranslations = new ArrayList<>();
    }

    @Override
    public void attachView(CardBrowserView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

        mSelectedSortMethod = (CardSortMethod) mUserData.getValue(UserDataRepository.SELECTED_CARD_SORT_METHOD,
                mCardSortMethods.get(0));


        mCardSortSelectionIndex = mSelectedSortMethod.getId();

        if (showingDeckCards) {
           // mCardsList = mCardsRep.getCardsByDeckDB(currentDeckChoosen);
            getViewState().showDeckCardview(currentDeckChoosen);
            getViewState().changeAddButtonResource(false);
        } else {
            getViewState().changeAddButtonResource(true);
        }

        if (editDeckMode)
            getViewState().updateCardColor(selectedColor);

        getViewState().switchDeckDisplayMode(editDeckMode);
        getViewState().switchCornerButtonState(showingDeckCards);
        loadSortedCards();
    }

    @Override
    public void detachView(CardBrowserView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }


    void initSortingMethods(String[] names) {
        mCardSortMethods = new ArrayList<>(names.length);
        for (int i = 0; i < names.length; i++) {
            CardSortMethod sortMethod = new CardSortMethod(i, names[i], true);
            mCardSortMethods.add(sortMethod);
        }
    }

    private void updateCounters() {
        if (showingDeckCards)
            getViewState().setupCounters(mCardsRep.getNewCardsByDeckDB(currentDeckChoosen).size(),
                    mCardsRep.getOldReadyForTrainCardsByDeckDB(currentDeckChoosen).size());
        else
            getViewState().setupCounters(mCardsRep.getNewCardsDB().size(), mCardsRep.getOldReadyForTrainCardsDB().size());
    }


    void sortButtonPressed() {
        getViewState().showSortOptionsDialog(mCardSortMethods, mSelectedSortMethod, mCardSortSelectionIndex);
    }

    void addButtonPressed() {
        if (showingDeckCards)
            getViewState().showAddCardDialog(mDecksRep.getDecksFromDB(), currentDeckChoosen);
        else
            getViewState().openDeckCreationFragment();
    }

    void addNewCardRequest(String front, String back, Language firstLanguage, Language secondLanguage, String cardContext, String chosenDeckName, int defaultColor) {
        Deck deck = mDecksRep.getDeckByName(chosenDeckName);
        Card card = new Card(-1, front, back, firstLanguage, secondLanguage, deck, cardContext);
        if (mCardsRep.containsSimilarCardInDeckDB(card, deck))
            getViewState().showCardAlreadyExistsSnackbarMessageAction(deck.getName());
        else {
            mCardsRep.insertCardToDB(card);
            loadSortedCards();
            getViewState().showCardSuccessfulyAddedSnackbarMessage(deck.getName());
        }
    }

    @Override
    public void onSnackbarEvent(int actionId) {
        if (actionId == 1)
            getViewState().showAddCardDialog(mDecksRep.getDecksFromDB(), currentDeckChoosen);

        else if (actionId == 2) {

            for (int i = 0; i < temporaryCards.size(); i++) {
                Card card = new Card(-1, temporaryCards.get(i));
                Translation t = temporaryCardTranslations.get(i);
                mCardsRep.insertCardToDB(card);
                if (t != null)
                    mTransRep.updateTranslationFavoriteStateDB(t, t.getFromText(), t.getToText(), true, card);

                loadSortedCards();
            }
        }

    }

    void decksButtonPressed() {
        getViewState().showDecksListDialog(mDecksRep.getDecksFromDB());
    }

    void backButtonPressed() {
        if (selectItemsForDeletionMode) {
            selectItemsForDeletionMode = false;
            getViewState().disableEditModeToolbar();
        } else {
            if (editDeckMode) {
                editDeckMode = false;
                getViewState().switchDeckDisplayMode(editDeckMode);
            } else {
                showingDeckCards = false;
                currentDeckChoosen = null;
                getViewState().switchCornerButtonState(showingDeckCards);
                //mCardsList = mCardsRep.getCardsFromDB();
                getViewState().hideDeckCardview();
                getViewState().changeAddButtonResource(true);
                loadSortedCards();
            }
        }
    }

    void createNewDeckRequest() {
        getViewState().openDeckCreationFragment();
    }


    public void cardsRecyclerItemPressed(int pos) {
        Card card = mCardsList.get(pos);
        getViewState().showEditCardDialog(mCardsList.get(pos), mDecksRep.findDecksByTranslationDirection(card.getTranslationDirection()));
    }

    public void cardsRecyclerItemLongPressed(int pos) {
        if (!editDeckMode) {
            selectItemsForDeletionMode = true;
            getViewState().enableEditModeToolbar(pos);
        }
    }

    public void updateSelectedItemsCount(int count, int max) {
        getViewState().updateSelectedCounter(count, max);
    }

    void editCardRequest(Card card, String front,
                         String back, String cardContext, String chosenDeckName) {
        Deck chosenDeck = mDecksRep.getDeckByName(chosenDeckName);
        Card updatedCard = new Card(-1, front, back, card.getFrontLanguage(), card.getBackLanguage(),
                chosenDeck, cardContext);
        String initialCardFront = card.getFront();
        String initialCardBack = card.getBack();
        Translation t = mTransRep.findTranslationByCardInDB(card);

        if (mCardsRep.containsSimilarCardInDeckDB(updatedCard, chosenDeck)) {
            getViewState().showCardAlreadyExistsSnackbarMessage(chosenDeckName);
        } else {
            mCardsRep.updateCardInDB(card, front, back, cardContext, chosenDeck);
            if ((!initialCardFront.equals(front) || !initialCardBack.equals(back)) &&
                    t != null) {
                mTransRep.updateTranslationFavoriteStateDB(t, front, back, true, card);
            }
            loadSortedCards();
        }

    }

    public void decksDialogRecyclerItemPressed(int cardId) {
        currentDeckChoosen = mDecksRep.getDeckById(cardId);
        Log.d(TAG, "decksDialogRecyclerItemPressed: " + currentDeckChoosen.getName());
        getViewState().hideDecksListDialog();
        noDecksInDB = false;
        getViewState().showDeckCardview(currentDeckChoosen);
        showingDeckCards = true;
        getViewState().switchCornerButtonState(showingDeckCards);
        getViewState().changeAddButtonResource(false);
        //mCardsList = mCardsRep.getCardsByDeckDB(currentDeckChoosen);
        loadSortedCards();
    }


    void editDeckButtonPressed() {
        editDeckMode = true;
        if (currentDeckChoosen != null) {
            selectedColor = currentDeckChoosen.getColor();
        }
        getViewState().switchDeckDisplayMode(editDeckMode);
    }

    void confirmEditDeckRequest(String updatedDeckName) {
        editDeckMode = false;
        if (!updatedDeckName.isEmpty()) {
            if (!updatedDeckName.equals(currentDeckChoosen.getName()) || selectedColor != currentDeckChoosen.getColor()) {
                mDecksRep.updateDeckNameAndColor(currentDeckChoosen, updatedDeckName, selectedColor);
                /*mCardsList = mCardsRep.getCardsByDeckDB(currentDeckChoosen);
                loadCards();*/
                loadSortedCards();
            }
            getViewState().switchDeckDisplayMode(editDeckMode);
        } else {
            getViewState().showDeckNameEditTextMessage("Введите название");
        }
    }

    void editDeckColorRequest() {
        getViewState().showSelectColorDialog(currentDeckChoosen);
    }

    void editDeckColorResultPassed(int color) {
        selectedColor = color;
        getViewState().updateCardColor(selectedColor);
    }

    List<Language> getLanguagesByDeckName(String deckName) {
        Deck deck = mDecksRep.getDeckByName(deckName);
        Language firstLang = deck.getFirstLanguage();
        Language secondLang = deck.getSecondLanguage();
        return Arrays.asList(firstLang, secondLang);
    }

    public void sortDialogRecyclerItemPressed(int pos) {
        getViewState().hideSortOptionstDialog();
        if (mCardSortSelectionIndex != pos) {
            mSelectedSortMethod = mCardSortMethods.get(pos);
            mCardSortSelectionIndex = pos;
            mUserData.putValue(UserDataRepository.SELECTED_CARD_SORT_METHOD, mSelectedSortMethod);
            loadSortedCards();
        } else {
            if (mSelectedSortMethod.isAscending())
                mSelectedSortMethod.setAscending(false);
            else
                mSelectedSortMethod.setAscending(true);
            mUserData.putValue(UserDataRepository.SELECTED_CARD_SORT_METHOD, mSelectedSortMethod);
            loadSortedCards();
        }
    }

    void deleteItemsRequest(boolean[] selectedItemsIndexes) {
        ArrayList<Card> cardsForDeletion = new ArrayList<>();
        ArrayList<Translation> cardTranlationsInHistory = new ArrayList<>();
        Card card;
        temporaryCards.clear();
        temporaryCardTranslations.clear();

        for (int i = 0; i < selectedItemsIndexes.length; i++)
            if (selectedItemsIndexes[i]) {
                card = mCardsList.get(i);
                temporaryCards.add(new TemporaryCard(card.getFront(), card.getBack(), card.getCardContext(), card.getTranslationDirection(),
                        card.getFrontLanguage(), card.getBackLanguage(), card.getDeck(), card.isReadyForTraining(), card.getLastTimeTrained(),
                        card.getTimesTrained(), card.isNew(), card.getNextTimeForTraining(), card.getLevel()));
                cardsForDeletion.add(card);

                Translation t = mTransRep.findTranslationByCardInDB(mCardsList.get(i));
                cardTranlationsInHistory.add(t);
                temporaryCardTranslations.add(t);
            }

        for (Translation t : cardTranlationsInHistory)
            if (t != null)
                mTransRep.updateTranslationFavoriteStateDB(t, t.getFromText(), t.getToText(), false, null);
        for (Card c : cardsForDeletion)
            mCardsRep.deleteCardFromDB(c);

        getViewState().disableEditModeToolbar();
        getViewState().showSelectedItemsDeletedMessage();
        loadSortedCards();
    }


    void resetItemsStatsButtonPressed(boolean[] selectedItemsIndexes) {
        getViewState().showResetStatsItemsConfirmationDialog(selectedItemsIndexes);
    }

    void resetItemsStatsRequestConfirmed(boolean[] selectedItemsIndexes) {
        ArrayList<Card> cardsForReset = new ArrayList<>();
        Card card;

        for (int i = 0; i < selectedItemsIndexes.length; i++)
            if (selectedItemsIndexes[i]) {
                card = mCardsList.get(i);
                cardsForReset.add(card);
            }

        for (Card c : cardsForReset)
            mCardsRep.resetCardTrainingStats(c);

        getViewState().disableEditModeToolbar();
        getViewState().showSelectedItemsStatsResetedMessage();
        loadSortedCards();

    }

    void deleteDeckButtonPressed() {
        getViewState().showDeleteDeckConfirmationDialog(currentDeckChoosen);
    }

    void deleteDeckRequestConfirmed() {
        for (Card c : mCardsList) {
            Translation t = mTransRep.findTranslationByCardInDB(c);
            if (t != null)
                mTransRep.updateTranslationFavoriteStateDB(t, t.getFromText(), t.getToText(), false, null);
            mCardsRep.deleteCardFromDB(c);
        }

        getViewState().showDeckDeletedMessage(currentDeckChoosen.getName());
        mDecksRep.deleteDeckFromDB(currentDeckChoosen);
        backButtonPressed();
    }

    void resetDeckButtonPressed() {
        getViewState().showResetDeckConfirmationDialog(currentDeckChoosen);
    }

    void resetDeckRequestConfirmed() {
        for (Card c : mCardsList) {
            mCardsRep.resetCardTrainingStats(c);
        }

        getViewState().showDeckResetedMessage(currentDeckChoosen.getName());
        loadSortedCards();
    }

    //=============Private logic===============

    private void loadSortedCards() {
        if (!showingDeckCards) {
            mCardsList = mCardsRep.getSortedCardsDB(mSelectedSortMethod);
        } else {
            mCardsList = mCardsRep.getSortedCardsByDeckDB(currentDeckChoosen, mSelectedSortMethod);
            Log.d(TAG, "loadSortedCards: " + mSelectedSortMethod.getId());
            getViewState().switchResetDeckButton(mCardsList.isEmpty());
        }

        getViewState().replaceCardsRecyclerData(mCardsList);
        updateCounters();
    }

}
