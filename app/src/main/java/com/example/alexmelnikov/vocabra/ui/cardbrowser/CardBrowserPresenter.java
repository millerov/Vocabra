package com.example.alexmelnikov.vocabra.ui.cardbrowser;

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

    private ArrayList<Card> mCardsList;
    private boolean showingDeckCards;


    public CardBrowserPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        mCardsList = mCardsRep.getCardsFromDB();
        showingDeckCards = false;
    }

    @Override
    public void attachView(CardBrowserView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        loadCards();
    }

    @Override
    public void detachView(CardBrowserView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    public void decksButtonPressed() {
        if (!showingDeckCards) {
            getViewState().showDecksListDialog(mDecksRep.getDecksFromDB());
        } else {
            showingDeckCards = false;
            getViewState().changeDeckButtonSrc(showingDeckCards);
            mCardsList = mCardsRep.getCardsFromDB();
            getViewState().hideDeckCard();
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
        Deck deck = mDecksRep.getDecksFromDB().get(pos);
        getViewState().hideDecksListDialog();
        getViewState().showDeckCard(deck);
        showingDeckCards = true;
        getViewState().changeDeckButtonSrc(showingDeckCards);

        mCardsList = mCardsRep.getCardsByDeckDB(deck);
        getViewState().replaceCardsRecyclerData(mCardsList);
    }

    //=============Private logic===============

    public void loadCards() {
        getViewState().replaceCardsRecyclerData(mCardsList);
    }

}
