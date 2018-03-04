package com.example.alexmelnikov.vocabra.ui.translator;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.TranslationsRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.SelectedLanguages;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.utils.LanguageUtils;
import com.example.alexmelnikov.vocabra.utils.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by AlexMelnikov on 27.02.18.
 */

@InjectViewState
public class TranslatorPresenter extends MvpPresenter<TranslatorView> {

    LanguagesRepository mLangRep;
    ArrayList<Language> mLangList;

    private int mSelectedFrom;
    private int mSelectedTo;

    private String mSelectedToLanguage;

    private String mInput = "";
    private String mOutput = "";

    private Translation mLastLoadedTranslation;

    private TranslationsRepository mTransRep;
    private UserDataRepository mUserData;

    public TranslatorPresenter() {
        mLangRep = new LanguagesRepository();
        mLangList = mLangRep.getLanguagesFromDB();
        mTransRep = new TranslationsRepository();
        mUserData = new UserDataRepository();
        Collections.sort(mLangList);

        SelectedLanguages selectedLanguages = (SelectedLanguages) mUserData.getValue(mUserData.SELECTED_LANGUAGES, new SelectedLanguages(
                mLangList.indexOf(LanguageUtils.findByKey("ru")),
                mLangList.indexOf(LanguageUtils.findByKey("en"))));

        Log.d("MyTag", "Got new value: " + selectedLanguages.from() + "-" + selectedLanguages.to());

        mSelectedFrom = selectedLanguages.from();
        mSelectedTo = selectedLanguages.to();


        try {
            mSelectedToLanguage = mLangList.get(mSelectedTo).getLang();
        } catch (NullPointerException e) {
            Log.e("MyTag", e.toString());
            mSelectedToLanguage = "Error";
        }
    }

    @Override
    public void attachView(TranslatorView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        getViewState().setupSpinners(mLangList, mSelectedFrom, mSelectedTo);
        getViewState().fillTextFields(mInput, mOutput, mSelectedToLanguage);
        loadData();
    }

    @Override
    public void detachView(TranslatorView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    public ArrayList<Language> getLanguages() {
        return mLangRep.getLanguagesFromDB();
    }


    public void inputChanges(String newValue) {
        mInput = newValue;
        getViewState().hideMessage();
    }

    public void translationRequested(String data, String langNameFrom, String langNameTo) {
        try {
            VocabraApp.getApiHelper().translateAsync(data, TextUtils.getTranslationDir(langNameFrom, langNameTo), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void translationResultPassed(Translation nextTranslation) {
        mLastLoadedTranslation = nextTranslation;
        mOutput = nextTranslation.getToText();
        getViewState().showTranslationResult(mOutput);
        getViewState().showMessage();
    }

    public void translationResultError() {
        getViewState().showTranslationResult("Error");
    }

    public void selectorFrom(int index) {
        if (index == mSelectedTo) {
            swapSelection();
        } else {
            mSelectedFrom = index;
            updateSelectedLangs();
        }
    }

    public void selectorTo(int index) {
        if (index == mSelectedFrom) {
            swapSelection();
        } else {
            mSelectedTo = index;
            updateSelectedLangs();
        }
    }


    private void swapSelection() {
        int temp = mSelectedFrom;
        mSelectedFrom = mSelectedTo;
        mSelectedTo = temp;
        getViewState().changeLanguagesSelected(mSelectedFrom, mSelectedTo);

        if (!mOutput.isEmpty()) {
            String tempStr = mInput;
            mInput = mOutput;
            mOutput = tempStr;
            getViewState().fillTextFields(mInput, mOutput, mSelectedToLanguage);
        }
         updateSelectedLangs();
    }

    public void clearInputPressed() {
        if (!mInput.isEmpty()) {
            mTransRep.insertTranslationToDB(mLastLoadedTranslation);
            wipeTextFields();
        }
    }

    private void loadData() {
        Log.d("Adapter", "loading new translations: size=" + mTransRep.getTranslationsFromDB().size());
        getViewState().replaceData(mTransRep.getTranslationsFromDB());
    }


    private void updateSelectedLangs() {
        SelectedLanguages newValue = new SelectedLanguages(mSelectedFrom, mSelectedTo);
        Log.d("MyTag", "Adding new value: " + mSelectedFrom + "-" + mSelectedTo);
        mUserData.putValue(mUserData.SELECTED_LANGUAGES, newValue);
    }

    private void wipeTextFields() {
        mLastLoadedTranslation = null;
        mInput = mOutput = "";
        getViewState().fillTextFields(mInput, mOutput, mSelectedToLanguage);
        getViewState().hideMessage();
    }
}
