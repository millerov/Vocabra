package com.example.alexmelnikov.vocabra.ui;

import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.Screens;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private int mCurrentFragmentIndex = 0;
    private int mPreviousFragmentIndex = 0;


    public void bottomNavigationClick(int itemPos) {
        mPreviousFragmentIndex = mCurrentFragmentIndex;
        Log.d("MyTag", "prev fragment index = " + mPreviousFragmentIndex);
        mCurrentFragmentIndex = itemPos;
        Log.d("MyTag", "current fragment index = " + mCurrentFragmentIndex);
        getViewState().replaceFragment(mCurrentFragmentIndex, mPreviousFragmentIndex);
    }

}
