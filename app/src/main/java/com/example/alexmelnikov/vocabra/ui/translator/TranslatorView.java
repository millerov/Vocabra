package com.example.alexmelnikov.vocabra.ui.translator;

import android.support.design.widget.BottomSheetBehavior;
import android.test.suitebuilder.annotation.SmallTest;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SingleStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseView;

import java.util.ArrayList;
import java.util.List;

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
    void fillTextFields(String from, String translated, String fromLang, String toLang);

    @StateStrategyType(SkipStrategy.class)
    void clearInputOutput();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void replaceHistoryData(ArrayList<Translation> translations);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void updateHistoryDataElement(int pos, Translation translation);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void openTranslationFragment(String fromText, String toText, String fromLang, String toLang);

    @StateStrategyType(SkipStrategy.class)
    void copyAction(String text);

    @StateStrategyType(SkipStrategy.class)
    void showFavoriteDropMessage();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showAddCardDialog(int pos, Translation translation, ArrayList<Deck> decks);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showTranslationCard();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void hideTranslationCard();
}
