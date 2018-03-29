package com.example.alexmelnikov.vocabra.ui.training;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseView;

/**
 * Created by AlexMelnikov on 29.03.18.
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface TrainingView extends BaseView {
    void showFrontView();
}
