package com.example.alexmelnikov.vocabra.ui.deck_add;

import android.support.design.widget.BottomSheetBehavior;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by AlexMelnikov on 15.03.18.
 */

public interface DeckAddView extends BaseView {

    @StateStrategyType(SkipStrategy.class)
    void setupSpinners(ArrayList<Language> languages, int from, int to);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void updateCardColor(int color);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showSelectColorDialog();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void changeLanguagesSelected(int from, int to);

    @StateStrategyType(SkipStrategy.class)
    void closeFragment();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showNameEditTextError(String message);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void hideNameEditTextError();

    @StateStrategyType(SkipStrategy.class)
    void setupDefaultColor();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void fillTextFields(String deckName);

}
