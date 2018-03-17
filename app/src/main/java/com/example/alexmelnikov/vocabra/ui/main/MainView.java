package com.example.alexmelnikov.vocabra.ui.main;

import android.support.design.widget.BottomSheetBehavior;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.ui.SnackBarActionHandler;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

public interface MainView  extends MvpView {
    int WORD_BROWSER_TAB_POS = R.id.word_browser_menu_item;
    int TRANSLATOR_TAB_POS = R.id.translator_menu_item;

    @StateStrategyType(OneExecutionStateStrategy.class)
    void replaceFragment(int prevIndex, int currentIndex);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showMessage(String message, boolean withAction, SnackBarActionHandler presenter);
}
