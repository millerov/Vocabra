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

    public CardBrowserPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
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
        getViewState().showDecksListDialog(mDecksRep.getDecksFromDB());
    }

    public void createNewDeckRequest() {
        getViewState().openDeckCreationFragment();
    }


    public void cardsRecyclerItemPressed(int pos) {
        Log.d(TAG, "cardsRecyclerItemPressed: " + mCardsRep.getCardsFromDB().get(pos).getFront());
    }

    public void decksDialogRecyclerItemPressed(int pos) {
        getViewState().hideDecksListDialog();
        getViewState().showDeckCard(mDecksRep.getDecksFromDB().get(pos));
    }

    //=============Private logic===============

    public void loadCards() {
        getViewState().replaceCardsRecyclerData(mCardsRep.getCardsFromDB());
    }

}
