package com.example.alexmelnikov.vocabra.ui.deck_add;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.LanguagesRepository;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;
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

    @Inject
    LanguagesRepository mLangRep;
    @Inject
    UserDataRepository mUserData;

    ArrayList<Language> mLangList;

    private int mSelectedFrom; //TranslatonFragment spinner index
    private int mSelectedTo; //TranslationFragment spinner index

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
    }

    @Override
    public void detachView(DeckAddView view) {
        super.detachView(view);
    }

}
