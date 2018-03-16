package com.example.alexmelnikov.vocabra.ui.deck_add;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.SelectedLanguages;
import com.example.alexmelnikov.vocabra.utils.LanguageUtils;

import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 15.03.18.
 */

@InjectViewState
public class DeckAddPresenter extends MvpPresenter<DeckAddView> {

    private static final String TAG = "MyTag";

    @Inject LanguagesRepository mLangRep;
    @Inject UserDataRepository mUserData;
    @Inject DecksRepository mDecksRep;

    ArrayList<Language> mLangList;

    public static int selectedColor;

    private int mSelectedFrom; //TranslatonFragment spinner index
    private int mSelectedTo; //TraxnslationFragment spinner index
    private String mSelectedToLanguage; //e.g. "Английский"
    private String mSelectedFromLanguage; //e.g. "Русский"

    public DeckAddPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        mLangList = mLangRep.getLanguagesFromDB();
        Collections.sort(mLangList);

        SelectedLanguages selectedLanguages = (SelectedLanguages) mUserData.getValue(mUserData.SELECTED_LANGUAGES, new SelectedLanguages(
                mLangList.indexOf(LanguageUtils.findByKey("ru")),
                mLangList.indexOf(LanguageUtils.findByKey("en"))));

        mSelectedFrom = selectedLanguages.from();
        mSelectedTo = selectedLanguages.to();

    }

    @Override
    public void attachView(DeckAddView view) {
        super.attachView(view);
        Log.d(TAG, "attachView: " + mSelectedFrom + "-" + mSelectedTo + "/" + mLangList.size());

        getViewState().setupSpinners(mLangList, mSelectedFrom, mSelectedTo);
        getViewState().attachInputListeners();
        getViewState().setupDefaultColor();
    }

    @Override
    public void detachView(DeckAddView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    public void selectorFrom(int index) {
        if (index == mSelectedTo) {
            swapSelection();
        } else {
            mSelectedFrom = index;
            updateSelectedLanguages();
        }
    }

    public void selectorTo(int index) {
        if (index == mSelectedFrom) {
            swapSelection();
        } else {
            mSelectedTo = index;
            updateSelectedLanguages();
        }
    }

    public void swapSelection() {
        int temp = mSelectedFrom;
        mSelectedFrom = mSelectedTo;
        mSelectedTo = temp;
        getViewState().changeLanguagesSelected(mSelectedFrom, mSelectedTo);

        updateSelectedLanguages();
    }


    public void colorChangeButtonPressed() {
        getViewState().showSelectColorDialog();
    }

    public void updateSelectedColor(int color) {
        selectedColor = color;
        getViewState().updateCardColor(color);
    }


    //
    public void addNewDeckRequest(String name) {
        if (!name.trim().isEmpty()) {
            Deck deck = new Deck(-1, name.trim(), selectedColor,
                    mLangList.get(mSelectedFrom), mLangList.get(mSelectedTo));
            if (!mDecksRep.containsSimilarElementInDB(deck)) {
                mDecksRep.insertDeckToDB(deck);
                getViewState().closeFragment();
            } else {
                getViewState().showEditTextError("Колода с таким названием уже существует");
            }
        } else {
            getViewState().showEditTextError("Введите название");
        }
    }

    //==================Private logic=================

    private void updateSelectedLanguages() {
        try {
            mSelectedToLanguage = mLangList.get(mSelectedTo).getLang();
            mSelectedFromLanguage = mLangList.get(mSelectedFrom).getLang();
        } catch (NullPointerException e) {
            mSelectedToLanguage = mSelectedFromLanguage = "Error";
        }
        Log.d(TAG, "updateSelectedLanguages: " + mSelectedFromLanguage + "-" + mSelectedToLanguage);
    }

}
