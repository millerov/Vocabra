package com.example.alexmelnikov.vocabra.ui;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

/**
 * Created by AlexMelnikov on 03.03.18.
 */

public interface BaseView extends MvpView {

    @StateStrategyType(SkipStrategy.class)
    void attachInputListeners();

    // Unsubscribe presenter from input events
    @StateStrategyType(SkipStrategy.class)
    void detachInputListeners();
}
