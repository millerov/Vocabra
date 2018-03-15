package com.example.alexmelnikov.vocabra.ui.deck_add;

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

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showSelectColorDialog();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void changeLanguagesSelected(int from, int to);

    @StateStrategyType(SkipStrategy.class)
    void closeFragment();

    @StateStrategyType(SkipStrategy.class)
    void showEditTextError(String message);

}
