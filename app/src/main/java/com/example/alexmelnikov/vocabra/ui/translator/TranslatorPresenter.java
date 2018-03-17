package com.example.alexmelnikov.vocabra.ui.translator;

import android.content.ClipboardManager;
import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.CardsRepository;
import com.example.alexmelnikov.vocabra.data.DecksRepository;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.TranslationsRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.SelectedLanguages;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.Translating;
import com.example.alexmelnikov.vocabra.utils.LanguageUtils;
import com.example.alexmelnikov.vocabra.utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 27.02.18.
 */

@InjectViewState
public class TranslatorPresenter extends MvpPresenter<TranslatorView> implements Translating {

    private static final String TAG = "TranslatorPresenter";

    @Inject
    LanguagesRepository mLangRep;
    @Inject
    TranslationsRepository mTransRep;
    @Inject
    UserDataRepository mUserData;
    @Inject
    CardsRepository mCardsRep;
    @Inject
    DecksRepository mDecksRep;

    ArrayList<Language> mLangList;

    private int mSelectedFrom; //TranslatonFragment spinner index
    private int mSelectedTo; //TranslationFragment spinner index

    private String mSelectedToLanguage; //e.g. "Английский"
    private String mSelectedFromLanguage; //e.g. "Русский"

    private String mInput = "";
    private String mOutput = "";

    private Translation mLastLoadedTranslation;

    public TranslatorPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        mLangList = mLangRep.getLanguagesFromDB();
        Collections.sort(mLangList);

        SelectedLanguages selectedLanguages = (SelectedLanguages) mUserData.getValue(mUserData.SELECTED_LANGUAGES, new SelectedLanguages(
                mLangList.indexOf(LanguageUtils.findByKey("ru")),
                mLangList.indexOf(LanguageUtils.findByKey("en"))));

        mSelectedFrom = selectedLanguages.from();
        mSelectedTo = selectedLanguages.to();

        updateSelectedLanguages();
    }

    @Override
    public void attachView(TranslatorView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        getViewState().setupSpinners(mLangList, mSelectedFrom, mSelectedTo);
        getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
        loadHistoryData();
    }

    @Override
    public void detachView(TranslatorView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    public ArrayList<Language> getLanguages() {
        return mLangRep.getLanguagesFromDB();
    }


    public void setInputOutput(Translation translation) {
        if (translation != null && translation.getFromText() != null) {
            mLastLoadedTranslation = translation;
            mInput = translation.getFromText();
            mOutput = translation.getToText();
            if (mOutput != "" || mInput == mOutput)
                updateDatabase();
        } else {
            mInput = "";
            mOutput = "";
        }
        getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
    }

    public void translationRequested(String data) {
        try {
            VocabraApp.getApiHelper().translateAsync(data, TextUtils.getTranslationDir(mSelectedFromLanguage, mSelectedToLanguage), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void translationResultPassed(Translation nextTranslation) {
        mLastLoadedTranslation = nextTranslation;
        mOutput = nextTranslation.getToText();
        getViewState().showTranslationResult(mOutput);
    }

    public void translationResultError() {
        getViewState().showTranslationResult("Error");
    }

    public void selectorFrom(int index) {
        if (index == mSelectedTo) {
            swapSelection();
        } else {
            mSelectedFrom = index;
            updateSelectedLangsIndexes();
            updateSelectedLanguages();
            getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
            if (!mInput.isEmpty() && mLastLoadedTranslation != null) {
                translationRequested(mInput);
            }
        }
    }

    public void selectorTo(int index) {
        if (index == mSelectedFrom) {
            swapSelection();
        } else {
            mSelectedTo = index;
            updateSelectedLangsIndexes();
            updateSelectedLanguages();
            getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
            if (!mInput.isEmpty() && mLastLoadedTranslation != null) {
                translationRequested(mInput);
            }
        }
    }

    public void clearButtonPressed() {
        if (!mInput.isEmpty()) {
            mLastLoadedTranslation = null;
            mInput = mOutput = "";
            getViewState().clearInputOutput();
        }
    }


    public void swapSelection() {
        int temp = mSelectedFrom;
        mSelectedFrom = mSelectedTo;
        mSelectedTo = temp;
        getViewState().changeLanguagesSelected(mSelectedFrom, mSelectedTo);

        if (!mOutput.isEmpty()) {
            String tempStr = mInput;
            mInput = mOutput;
            mOutput = tempStr;
        }

        updateSelectedLangsIndexes();
        updateSelectedLanguages();
        getViewState().fillTextFields(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
    }

    public void inputRequested() {
        getViewState().openTranslationFragment(mInput, mOutput, mSelectedFromLanguage, mSelectedToLanguage);
    }

    public void copyButtonPressed() {
        getViewState().copyAction(mOutput);
    }


    public void addNewCardFromHistoryRequest(int pos) {
        getViewState().showAddCardDialog(pos, mTransRep.getTranslationsFromDB().get(pos),
                mDecksRep.findDecksByTranslationDirection(mTransRep.getTranslationsFromDB().get(pos).getLangs()));
    }

    //===============
    //ADD DECK SUPPORT
    //===============
    public void addNewCardFromHistoryResultPassed(int pos, Translation initialTranslation, String front,
                                                       String back, String context) {
        //check if translation from db texts equal to set by user front and back
        //if not, update translation element in db also update history element on view
        if (initialTranslation.getFromText().equals(front)
                && initialTranslation.getToText().equals(back)) {
            mTransRep.updateTranslationFavoriteStateDB(initialTranslation, initialTranslation.getFromText(),
                    initialTranslation.getToText(), true);
            getViewState().updateHistoryDataElement(pos, initialTranslation);
        } else {
            mTransRep.updateTranslationFavoriteStateDB(initialTranslation, front, back, true);
            getViewState().updateHistoryDataElement(pos, initialTranslation);
        }
    }

    //==================Private logic=================

    private void loadHistoryData() {
        getViewState().replaceHistoryData(mTransRep.getTranslationsFromDB());
    }


    private void updateSelectedLangsIndexes() {
        SelectedLanguages newValue = new SelectedLanguages(mSelectedFrom, mSelectedTo);
        mUserData.putValue(mUserData.SELECTED_LANGUAGES, newValue);
    }

    private void updateSelectedLanguages() {
        try {
            mSelectedToLanguage = mLangList.get(mSelectedTo).getLang();
            mSelectedFromLanguage = mLangList.get(mSelectedFrom).getLang();
        } catch (Exception e) {
            mSelectedToLanguage = mSelectedFromLanguage = "Error";
        }
    }

    private void updateDatabase() {
        if (!mTransRep.containsSimilarElementInDB(mLastLoadedTranslation)) {
            mTransRep.insertTranslationToDB(mLastLoadedTranslation);
        } else {
            mTransRep.deleteTranslationFromDB(mLastLoadedTranslation);
            mTransRep.insertTranslationToDB(mLastLoadedTranslation);
        }
    }

    private void clearDatabase() {
        mTransRep.clearTranslationsDB();
    }
}
