package com.example.alexmelnikov.vocabra.ui.cardbrowser;

import android.support.annotation.Nullable;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.AddToEndStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.model.Card;
import com.example.alexmelnikov.vocabra.model.CardSortMethod;
import com.example.alexmelnikov.vocabra.model.Deck;
import com.example.alexmelnikov.vocabra.model.Translation;
import com.example.alexmelnikov.vocabra.ui.BaseView;

import java.util.ArrayList;

/**
 * Created by AlexMelnikov on 10.03.18.
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface CardBrowserView extends BaseView {

    @StateStrategyType(SkipStrategy.class)
    void replaceCardsRecyclerData(ArrayList<Card> cards);

    void showDecksListDialog(ArrayList<Deck> decks);

    void hideDecksListDialog();

    void openDeckCreationFragment();

    void showDeckCardview(Deck deck);

    void hideDeckCardview();

    void switchCornerButtonState(boolean showingDeckCards);

    void switchDeckDisplayMode(boolean editModeOn);

    void showSelectColorDialog(Deck currentDeck);

    void updateCardColor(int color);

    void showDeckNameEditTextMessage(String message);

    void showAddCardDialog(ArrayList<Deck> decks, @Nullable Deck currentDeck);

    void showEditCardDialog(int pos, Card card, ArrayList<Deck> decks);

    void showCardAlreadyExistsSnackbarMessageAction(String deckName);

    void showCardAlreadyExistsSnackbarMessage(String deckName);

    void showCardSuccessfulyAddedSnackbarMessage(String deckName);

    void showSortOptionsDialog(ArrayList<CardSortMethod> methods,
                               CardSortMethod currentMethod, int currentMethodIndex);

    void hideSortOptionstDialog();

    void enableEditModeToolbar();

    void disableEditModeToolbar();

}
