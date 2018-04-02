package com.example.alexmelnikov.vocabra.ui.training;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseView;

/**
 * Created by AlexMelnikov on 29.03.18.
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface TrainingView extends BaseView {
    void showFront(String front, boolean firstAttach);

    void showBack(String back, @Nullable String context);

    void hideCurrentFrontAndBack();

    void showFrontAndBackOfPrevCard(String front, String back, @Nullable String context);

    void showOptions(boolean withHardBtn);

    void hideOptions(boolean withHardBtn);

    void fillOptionsTextViews(boolean withHardBtn, String easyTime, String goodTime,
                              String forgotTime, String hardTime);

    void fillCounters(int newCardsCount, int oldReadyCardsCount);

    void closeFragment();

}
