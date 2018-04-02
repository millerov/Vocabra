package com.example.alexmelnikov.vocabra.ui.decks_for_train;

import android.view.View;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by AlexMelnikov on 28.03.18.
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface DecksForTrainingView extends BaseView {

    void replaceCardsRecyclerData(ArrayList<Deck> decks);

    void openTrainingActivity(Deck deck, View item, String transitionName);

    void setupTextView(int counter);

    void showEmptyDeckSelectedMessage(View item);

}
