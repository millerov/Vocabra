package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.support.annotation.Nullable;
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
import com.example.alexmelnikov.vocabra.ui.SnackBarActionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 10.03.18.
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

    public static int selectedColor;

    private ArrayList<Card> mCardsList;
    private boolean showingDeckCards;
    @Nullable private Deck currentDeckChoosen;

    private boolean editDeckMode;

    private int mCardSortSelectionIndex;
    private ArrayList<CardSortMethod> mCardSortMethods;
    private CardSortMethod mSelectedSortMethod;

    private boolean selectItemsForDeletionMode;

    public CardBrowserPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        showingDeckCards = false;
        editDeckMode = false;
    }

    @Override
    public void attachView(CardBrowserView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

        mSelectedSortMethod = (CardSortMethod) mUserData.getValue(mUserData.SELECTED_CARD_SORT_METHOD,
                mCardSortMethods.get(0));


        mCardSortSelectionIndex = mSelectedSortMethod.getId();

        if (showingDeckCards) {
           // mCardsList = mCardsRep.getCardsByDeckDB(currentDeckChoosen);
            getViewState().showDeckCardview(currentDeckChoosen);
        } /*else {
            mCardsList = mCardsRep.getCardsFromDB();
        }*/

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


    public void initSortingMethods(String[] names) {
        mCardSortMethods = new ArrayList<CardSortMethod>(names.length);
        for (int i = 0; i < names.length; i++) {
            CardSortMethod sortMethod = new CardSortMethod(i, names[i], true);
            mCardSortMethods.add(sortMethod);
        }
    }

    public void sortButtonPressed() {
        getViewState().showSortOptionsDialog(mCardSortMethods, mSelectedSortMethod, mCardSortSelectionIndex);
    }

    public void addCardButtonPressed() {
        getViewState().showAddCardDialog(mDecksRep.getDecksFromDB(), currentDeckChoosen);
    }

    public void addNewCardRequest(String front, String back, Language firstLanguage, Language secondLanguage, String cardContext, String chosenDeckName, int defaultColor) {
        Deck deck = mDecksRep.getDeckByName(chosenDeckName);
        Card card = new Card(-1, front, back, firstLanguage, secondLanguage, deck, cardContext);
        if (mCardsRep.containsSimilarCardInDeckDB(card, deck))
            getViewState().showCardAlreadyExistsSnackbarMessageAction(deck.getName());
        else {
            mCardsRep.insertCardToDB(card);
            /*mCardsList = mCardsRep.getCardsFromDB();
            loadCards();*/
            loadSortedCards();
            getViewState().showCardSuccessfulyAddedSnackbarMessage(deck.getName());
        }
    }

    @Override
    public void onSnackbarEvent() {
        getViewState().showAddCardDialog(mDecksRep.getDecksFromDB(), currentDeckChoosen);
    }

    public void decksButtonPressed() {
        getViewState().showDecksListDialog(mDecksRep.getDecksFromDB());
    }

    public void backButtonPressed() {
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
                loadSortedCards();
            }
        }
    }

    public void createNewDeckRequest() {
        getViewState().openDeckCreationFragment();
    }


    public void cardsRecyclerItemPressed(int pos) {
        Card card = mCardsList.get(pos);
        getViewState().showEditCardDialog(pos, mCardsList.get(pos),
                mDecksRep.findDecksByTranslationDirection(card.getTranslationDirection()));
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

    public void editCardRequest(Card card, String front,
                                String back, String cardContext, String chosenDeckName) {
        Deck chosenDeck = mDecksRep.getDeckByName(chosenDeckName);
        Card updatedCard = new Card(-1, front, back, card.getFrontLanguage(), card.getBackLanguage(),
                chosenDeck, cardContext);
        if (mCardsRep.containsSimilarCardInDeckDB(updatedCard, chosenDeck)) {
            getViewState().showCardAlreadyExistsSnackbarMessage(chosenDeckName);
        } else {
            mCardsRep.updateCardInDB(card, front, back, cardContext, chosenDeck);
            loadSortedCards();
        }

    }

    public void decksDialogRecyclerItemPressed(int pos) {
        currentDeckChoosen = mDecksRep.getDecksFromDB().get(pos);
        getViewState().hideDecksListDialog();
        getViewState().showDeckCardview(currentDeckChoosen);
        showingDeckCards = true;
        getViewState().switchCornerButtonState(showingDeckCards);

        //mCardsList = mCardsRep.getCardsByDeckDB(currentDeckChoosen);
        loadSortedCards();
    }


    public void editDeckButtonPressed() {
        editDeckMode = true;
        selectedColor = currentDeckChoosen.getColor();
        getViewState().switchDeckDisplayMode(editDeckMode);
    }

    public void confirmEditDeckRequest(String updatedDeckName) {
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

    public void editDeckColorRequest() {
        getViewState().showSelectColorDialog(currentDeckChoosen);
    }

    public void editDeckColorResultPassed(int color) {
        selectedColor = color;
        getViewState().updateCardColor(selectedColor);
    }

    public List<Language> getLanguagesByDeckName(String deckName) {
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
            mUserData.putValue(mUserData.SELECTED_CARD_SORT_METHOD, mSelectedSortMethod);
            loadSortedCards();
        } else {
            if (mSelectedSortMethod.isAscending())
                mSelectedSortMethod.setAscending(false);
            else
                mSelectedSortMethod.setAscending(true);
            mUserData.putValue(mUserData.SELECTED_CARD_SORT_METHOD, mSelectedSortMethod);
            loadSortedCards();
        }
    }

    public void deleteItemsRequest(boolean[] selectedItemsIndexes) {
        ArrayList<Card> cardsForDeletion = new ArrayList<Card>();
        ArrayList<Translation> cardTranlationsInHistory = new ArrayList<Translation>();
        for (int i = 0; i < selectedItemsIndexes.length; i++)
            if (selectedItemsIndexes[i]) {
                cardsForDeletion.add(mCardsList.get(i));
                cardTranlationsInHistory.add(mTransRep.findTranslationByCardInDB(mCardsList.get(i)));
            }

        for (Translation t : cardTranlationsInHistory)
            if (t != null)
                mTransRep.updateTranslationFavoriteStateDB(t, t.getFromText(), t.getToText(), false, null);
        for (Card c : cardsForDeletion)
            mCardsRep.deleteCardFromDB(c);

        getViewState().disableEditModeToolbar();
        loadSortedCards();
    }

    //=============Private logic===============

    private void loadSortedCards() {
        if (!showingDeckCards)
            mCardsList = mCardsRep.getSortedCardsDB(mSelectedSortMethod);
        else
            mCardsList = mCardsRep.getSortedCardsByDeckDB(currentDeckChoosen, mSelectedSortMethod);
        getViewState().replaceCardsRecyclerData(mCardsList);
    }

/*    private void loadCards() {
        getViewState().replaceCardsRecyclerData(mCardsList);
    }*/


}
