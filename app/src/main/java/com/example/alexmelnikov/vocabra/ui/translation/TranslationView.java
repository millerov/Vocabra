package com.example.alexmelnikov.vocabra.ui.translation;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseView;

/**
 * Created by AlexMelnikov on 08.03.18.
 */

public interface TranslationView extends BaseView {

    @StateStrategyType(SkipStrategy.class)
    void closeFragment(Translation translation);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void fillTextFields(String fromText, String toText, String fromLang, String toLang);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showTranslationResult(String result);

    @StateStrategyType(SkipStrategy.class)
    void clearInputOutput();
}
