package com.example.alexmelnikov.vocabra.ui.translation;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.Translating;
import com.example.alexmelnikov.vocabra.utils.TextUtils;

import java.io.IOException;

/**
 * TranslationPresenter.java – presenter for TranslationFragment
 * @author Alexander Melnikov
 */

@InjectViewState
public class TranslationPresenter extends MvpPresenter<TranslationView> implements Translating {

    private static final String TAG = "myTag";
    
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

    void setInputOutput(String fromText, String toText, String fromLang, String toLang) {
        mInput = fromText;
        mOutput = toText;
        mFromLanguage = fromLang;
        mToLanguage = toLang;
        getViewState().fillTextFields(fromText, toText, fromLang, toLang);
    }

    void continueRequest() {
        getViewState().closeFragment(mLastLoadedTranslation);
    }

    void inputChanges(String input) {
        mInput = input;
        translationRequested(input);
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

    void clearButtonPressed() {
        if (!mInput.isEmpty()) {
            mInput = mOutput = "";
            mLastLoadedTranslation = null;
            getViewState().clearInputOutput(true);
        } else {
            continueRequest();
        }
    }
}
