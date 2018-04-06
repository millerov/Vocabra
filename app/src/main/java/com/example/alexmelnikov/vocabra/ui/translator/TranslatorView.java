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

@StateStrategyType(OneExecutionStateStrategy.class)
public interface TranslatorView extends BaseView {

    void setupSpinners(ArrayList<Language> languages, int from, int to);

    void showTranslationResult(String result);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void changeLanguagesSelected(int from, int to);

    void fillTextFields(String from, String translated, String fromLang, String toLang);

    void clearInputOutput();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void replaceHistoryData(ArrayList<Translation> translations);


    void changeFavouriteButtonAppearance(boolean light);

    void updateHistoryDataElement(int pos, Translation translation);

    void openTranslationFragment(String fromText, String toText, String fromLang, String toLang);

    void copyAction(String text);

    void showFavoriteDropMessage();

    void showAddCardDialog(int pos, Translation translation, ArrayList<Deck> decks);

    void showTranslationCard();

    void hideTranslationCard();

    void showDeleteOptionsDialog(int pos);

    void showItemDeletedFromHistoryMessage();

    void showHistoryCleanedMessage();


}
