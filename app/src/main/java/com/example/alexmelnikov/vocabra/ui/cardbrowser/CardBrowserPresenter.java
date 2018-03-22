package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.graphics.Color;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.utils.LanguageUtils;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

@InjectViewState
public class CardBrowserPresenter extends MvpPresenter<CardBrowserView> {

    private static final String TAG = "MyTag";

    @Inject
    DecksRepository mDecksRep;
    @Inject
    LanguagesRepository mLangRep;
    @Inject
    CardsRepository mCardsRep;

    public static int selectedColor;

    private ArrayList<Card> mCardsList;
    private boolean showingDeckCards;
    private Deck currentDeckChoosen;

    private boolean editDeckMode;

    public CardBrowserPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        showingDeckCards = false;
        editDeckMode = false;
    }

    @Override
    public void attachView(CardBrowserView view) {
        super.attachView(view);
        getViewState().attachInputListeners();

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
        Log.d(TAG, "cardsRecyclerItemPressed: " + mCardsRep.getCardsFromDB().get(pos).getFront());
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

    //=============Private logic===============

    public void loadCards() {
        getViewState().replaceCardsRecyclerData(mCardsList);
    }

}
