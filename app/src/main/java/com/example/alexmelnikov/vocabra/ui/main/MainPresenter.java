package com.example.alexmelnikov.vocabra.ui.main;

import android.support.v4.app.Fragment;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private int mCurrentFragmentIndex = 0;
    private int mPreviousFragmentIndex = 0;


    @Override
    public void attachView(MainView view) {
        super.attachView(view);
    }

    public void bottomNavigationClick(int itemPos) {
        mPreviousFragmentIndex = mCurrentFragmentIndex;
        mCurrentFragmentIndex = itemPos;
        getViewState().replaceFragmentNavigationBar(mCurrentFragmentIndex, mPreviousFragmentIndex);
    }

}
