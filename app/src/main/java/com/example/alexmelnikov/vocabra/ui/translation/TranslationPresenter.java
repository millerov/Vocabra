package com.example.alexmelnikov.vocabra.ui.translation;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.Translating;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorView;
import com.example.alexmelnikov.vocabra.utils.TextUtils;

import java.io.IOException;

/**
 * Translation Fragment Presenter
 */

@InjectViewState
public class TranslationPresenter extends MvpPresenter<TranslationView> implements Translating {

    private static final String TAG = "TranslationPresenter";
    
    private String mInput = "";
    private String mOutput = "";

    private String mFromLanguage;
    private String mToLanguage;

    private Translation mLastLoadedTranslation;


    @Override
    public void attachView(TranslationView view) {
        mLastLoadedTranslation = null;
        super.attachView(view);
        getViewState().attachInputListeners();
    }

    @Override
    public void detachView(TranslationView view) {
        super.detachView(view);
        getViewState().detachInputListeners();
    }

    public void setInputOutput(String fromText, String toText, String fromLang, String toLang) {
        mInput = fromText;
        mOutput = toText;
        mFromLanguage = fromLang;
        mToLanguage = toLang;
        getViewState().fillTextFields(fromText, toText, fromLang, toLang);
    }

    public void continueRequest() {
        getViewState().closeFragment(mLastLoadedTranslation);
    }

    public void inputChanges(String input) {
        if (!input.trim().isEmpty())
            mInput = input;
    }

    public void translationRequested(String data) {
        try {
            VocabraApp.getApiHelper().translateAsync(data, TextUtils.getTranslationDir(mFromLanguage, mToLanguage), this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void translationResultPassed(Translation translation) {
        mLastLoadedTranslation = translation;
        mOutput = translation.getToText();
        getViewState().showTranslationResult(mOutput);
    }

    public void translationResultError() {
        mOutput = "Error";
        getViewState().showTranslationResult(mOutput);
    }

    public void clearButtonPressed() {
        if (!mInput.isEmpty()) {
            mInput = mOutput = "";
            mLastLoadedTranslation = null;
            getViewState().clearInputOutput();
        } else {
            continueRequest();
        }
    }
}
