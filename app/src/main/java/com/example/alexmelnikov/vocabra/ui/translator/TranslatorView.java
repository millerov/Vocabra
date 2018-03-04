package com.example.alexmelnikov.vocabra.ui.translator;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by AlexMelnikov on 27.02.18.
 */

public interface TranslatorView extends BaseView {

    @StateStrategyType(SkipStrategy.class)
    void setupSpinners(ArrayList<Language> languages, int from, int to);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showTranslationResult(String result);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void changeLanguagesSelected(int from, int to);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void fillTextFields(String from, String translated, String to);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showMessage();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void hideMessage();

    @StateStrategyType(SkipStrategy.class)
    void hideResults();

}
