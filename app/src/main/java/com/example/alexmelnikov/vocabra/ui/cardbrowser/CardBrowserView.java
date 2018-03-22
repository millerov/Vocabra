package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.support.annotation.Nullable;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

public interface CardBrowserView extends BaseView {

    @StateStrategyType(SkipStrategy.class)
    void replaceCardsRecyclerData(ArrayList<Card> cards);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showDecksListDialog(ArrayList<Deck> decks);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void hideDecksListDialog();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void openDeckCreationFragment(boolean withEditDeckAction, @Nullable String deckName);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showDeckCardview(Deck deck);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void hideDeckCardview();

    @StateStrategyType(OneExecutionStateStrategy.class)
    void changeDeckButtonSrc(boolean showingDeckCards);
}
