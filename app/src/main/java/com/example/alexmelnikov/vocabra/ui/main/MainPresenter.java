package com.example.alexmelnikov.vocabra.ui.main;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.data.UserDataRepository;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by AlexMelnikov on 25.02.18.
 */

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private static final String TAG = "MyTag";

    private int mCurrentFragmentIndex = 0;
    private int mPreviousFragmentIndex = 0;

    @Inject
    UserDataRepository mUserData;

    public MainPresenter() {
        VocabraApp.getPresenterComponent().inject(this);
        Date firstAppLaunchDate = (Date) mUserData.getValue(mUserData.FIRST_APP_LAUNCH_DATE, null);
        if (firstAppLaunchDate == null) {
            mUserData.putValue(mUserData.FIRST_APP_LAUNCH_DATE, new Date());
        }
    }

    @Override
    public void attachView(MainView view) {
        super.attachView(view);
       // bottomNavigationClick(mCurrentFragmentIndex);
    }

    public void bottomNavigationClick(int itemPos) {
        mPreviousFragmentIndex = mCurrentFragmentIndex;
        mCurrentFragmentIndex = itemPos;
        getViewState().replaceFragmentNavigationBar(mCurrentFragmentIndex, mPreviousFragmentIndex);
    }
}
