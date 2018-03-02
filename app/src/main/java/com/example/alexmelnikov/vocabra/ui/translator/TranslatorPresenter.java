package com.example.alexmelnikov.vocabra.ui.translator;

import android.widget.Spinner;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.utils.LanguageUtils;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 27.02.18.
 */

@InjectViewState
public class TranslatorPresenter extends MvpPresenter<TranslatorView> {

    LanguagesRepository mLangRep;

    public TranslatorPresenter() {
        mLangRep = new LanguagesRepository();
    }

    public ArrayList<Language> getLanguages() {
        return mLangRep.getLanguagesFromDB();
    }


    public String getTranslationDir(Spinner spinFrom, Spinner spinTo) {
        if (spinFrom.getSelectedItem() == null || spinTo.getSelectedItem() == null) {
            return null;
        }
        String from = LanguageUtils.findKeyByName(spinFrom.getSelectedItem().toString());
        String to = LanguageUtils.findKeyByName(spinTo.getSelectedItem().toString());
        return from + "-" + to;
    }

}
