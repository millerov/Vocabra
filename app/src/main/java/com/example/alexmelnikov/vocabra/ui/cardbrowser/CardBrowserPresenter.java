package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.CardSortMethod;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
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

    public static int selectedColor;

    private ArrayList<Card> mCardsList;
    private boolean showingDeckCards;
    private Deck currentDeckChoosen;

    private boolean editDeckMode;

    private int mCardSortSelectionIndex;
    private ArrayList<CardSortMethod> mCardSortMethods;
    private CardSortMethod mSelectedSortMethod;

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
            mCardsList = mCardsRep.getCardsByDeckDB(currentDeckChoosen);
            getViewState().showDeckCardview(currentDeckChoosen);
        } else {
            mCardsList = mCardsRep.getCardsFromDB();
        }

        if (editDeckMode)
            getViewState().updateCardColor(selectedColor);

        getViewState().switchDeckDisplayMode(editDeckMode);
        getViewState().switchCornerButtonState(showingDeckCards);
        loadCards();
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
        getViewState().showAddCardDialog(mDecksRep.getDecksFromDB());
    }

    public void addNewCardRequest(String front, String back, Language firstLanguage, Language secondLanguage, String cardContext, String chosenDeckName, int defaultColor) {
        Deck deck = mDecksRep.getDeckByName(chosenDeckName);
        Card card = new Card(-1, front, back, firstLanguage, secondLanguage, deck, cardContext);
        if (mCardsRep.containsSimilarCardInDeckDB(card, deck))
            getViewState().showCardAlreadyExistsSnackbarMessage(deck.getName());
        else {
            mCardsRep.insertCardToDB(card);
            mCardsList = mCardsRep.getCardsFromDB();
            loadCards();
            getViewState().showCardSuccessfulyAddedSnackbarMessage(deck.getName());
        }
    }

    @Override
    public void onSnackbarEvent() {
        getViewState().showAddCardDialog(mDecksRep.getDecksFromDB());
    }

    public void decksButtonPressed() {
        getViewState().showDecksListDialog(mDecksRep.getDecksFromDB());
    }

    public void backButtonPressed() {
        if (editDeckMode) {
            editDeckMode = false;
            getViewState().switchDeckDisplayMode(editDeckMode);
        } else {
            showingDeckCards = false;
            currentDeckChoosen = null;
            getViewState().switchCornerButtonState(showingDeckCards);
            mCardsList = mCardsRep.getCardsFromDB();
            getViewState().hideDeckCardview();
            getViewState().replaceCardsRecyclerData(mCardsList);
        }
    }

    public void createNewDeckRequest() {
        getViewState().openDeckCreationFragment();
    }


    public void cardsRecyclerItemPressed(int pos) {
        getViewState().showEditCardDialog(pos, mCardsList.get(pos), mDecksRep.getDecksFromDB());
    }

    public void decksDialogRecyclerItemPressed(int pos) {
        currentDeckChoosen = mDecksRep.getDecksFromDB().get(pos);
        getViewState().hideDecksListDialog();
        getViewState().showDeckCardview(currentDeckChoosen);
        showingDeckCards = true;
        getViewState().switchCornerButtonState(showingDeckCards);

        mCardsList = mCardsRep.getCardsByDeckDB(currentDeckChoosen);
        getViewState().replaceCardsRecyclerData(mCardsList);
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
                mCardsList = mCardsRep.getCardsByDeckDB(currentDeckChoosen);
                loadCards();
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


    //=============Private logic===============

    private void loadCards() {
        getViewState().replaceCardsRecyclerData(mCardsList);
    }

}
