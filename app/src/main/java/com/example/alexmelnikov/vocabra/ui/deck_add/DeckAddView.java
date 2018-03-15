package com.example.alexmelnikov.vocabra.ui.deck_add;

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Language;
import com.example.alexmelnikov.vocabra.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by AlexMelnikov on 15.03.18.
 */

public interface DeckAddView extends BaseView {

    @StateStrategyType(SkipStrategy.class)
    void setupSpinners(ArrayList<Language> languages, int from, int to);

}
