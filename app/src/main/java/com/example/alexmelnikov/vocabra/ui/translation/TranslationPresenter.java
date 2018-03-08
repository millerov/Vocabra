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
 * Created by AlexMelnikov on 08.03.18.
 */

@InjectViewState
public class TranslationPresenter extends MvpPresenter<TranslationView> implements Translating {

    private String mInput = "";
    private String mOutput = "";

    private String mFromLanguage;
    private String mToLanguage;


    @Override
    public void attachView(TranslationView view) {
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

    public void translationRequest() {
        getViewState().closeFragment(mInput, mOutput);
    }

    public void inputChanges(String input) {
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
        mOutput = translation.getToText();
        getViewState().showTranslationResult(mOutput);
    }

    public void translationResultError() {
        mOutput = "Error";
        getViewState().showTranslationResult(mOutput);
    }



}
