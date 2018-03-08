package com.example.alexmelnikov.vocabra.ui.translation;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorView;

/**
 * Created by AlexMelnikov on 08.03.18.
 */

@InjectViewState
public class TranslationPresenter extends MvpPresenter<TranslationView> {

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


}
