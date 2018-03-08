package com.example.alexmelnikov.vocabra.ui;

import android.support.v4.app.Fragment;

import com.arellomobile.mvp.MvpAppCompatActivity;

import java.util.List;

/**
 * onBackPressed overrided so fragment could also override this method
 */

public class BaseActivity extends MvpAppCompatActivity {
    @Override
    public void onBackPressed() {

        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        for(Fragment f : fragmentList) {
            if(f instanceof BaseFragment) {
                handled = ((BaseFragment)f).onBackPressed();

                if(handled) {
                    break;
                }
            }
        }

        if(!handled) {
            super.onBackPressed();
        }
    }
}
