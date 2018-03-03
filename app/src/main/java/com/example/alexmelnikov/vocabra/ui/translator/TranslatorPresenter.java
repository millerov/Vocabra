package com.example.alexmelnikov.vocabra.ui.translator;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.model.Language;
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

    //private Translation mLastLoadedTranslation;

    public TranslatorPresenter() {
        mLangRep = new LanguagesRepository();
        mLangList = mLangRep.getLanguagesFromDB();
        Collections.sort(mLangList);
        //Setting default values "ru"/"en"
        mSelectedFrom = mLangList.indexOf(LanguageUtils.findByKey("ru"));
        mSelectedTo = mLangList.indexOf(LanguageUtils.findByKey("en"));
        mSelectedToLanguage = mLangList.get(mSelectedTo).getLang();
    }

    @Override
    public void attachView(TranslatorView view) {
        super.attachView(view);
        getViewState().attachInputListeners();
        getViewState().setupSpinners(mLangList, mSelectedFrom, mSelectedTo);
        getViewState().fillTextFields(mInput, mOutput, mSelectedToLanguage);
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

    public void translationResultPassed(String translationRes) {
        mOutput = translationRes;
        getViewState().showTranslationResult(mOutput);
    }

    public void selectorFrom(int index) {
        if (index == mSelectedTo) {
            swapSelection();
        } else {
            mSelectedFrom = index;
           // updateSelectedLangs();
        }
    }

    public void selectorTo(int index) {
        if (index == mSelectedTo) {
            swapSelection();
        } else {
            mSelectedTo = index;
            // updateSelectedLangs();
        }
    }


    private void wipeTextFields() {
        mInput = mOutput = "";
        getViewState().fillTextFields(mInput, mOutput, mSelectedToLanguage);
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
        // updateSelectedLangs();
    }
}
