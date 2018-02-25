package com.example.alexmelnikov.vocabra.ui;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.R;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface MainView  extends MvpView {
    int WORD_BROWSER_TAB_POS = R.id.word_browser_menu_item;
    int TRANSLATOR_TAB_POS = R.id.translator_menu_item;
}
