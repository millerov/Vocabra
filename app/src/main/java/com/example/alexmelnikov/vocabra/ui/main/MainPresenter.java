package com.example.alexmelnikov.vocabra.ui.main;

import android.util.Log;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.ui.cardbrowser.CardBrowserFragment;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private int mCurrentFragmentIndex = 0;
    private int mPreviousFragmentIndex = 0;


    public void bottomNavigationClick(int itemPos) {
        mPreviousFragmentIndex = mCurrentFragmentIndex;
        mCurrentFragmentIndex = itemPos;
        getViewState().replaceFragment(mCurrentFragmentIndex, mPreviousFragmentIndex);
    }

}
