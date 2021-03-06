package com.example.alexmelnikov.vocabra.ui.main;

import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.view.View;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.ui.SnackBarActionHandler;
import com.example.alexmelnikov.vocabra.ui.cardbrowser.CardBrowserFragment;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

@StateStrategyType(OneExecutionStateStrategy.class)
public interface MainView  extends MvpView {
    int WORD_BROWSER_TAB_POS = R.id.word_browser_menu_item;
    int TRANSLATOR_TAB_POS = R.id.translator_menu_item;

    void replaceFragmentNavigationBar(int prevIndex, int currentIndex);


    void showMessage(int actionId, String message, boolean withAction, @Nullable SnackBarActionHandler presenter, @Nullable String actionText);

    void hideBottomNavigationBar();

    void showBottomNavigationBar();
}
