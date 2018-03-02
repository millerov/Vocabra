package com.example.alexmelnikov.vocabra.ui.main;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.example.alexmelnikov.vocabra.VocabraApp;
import com.example.alexmelnikov.vocabra.ui.translator.TranslatorFragment;
import com.example.alexmelnikov.vocabra.ui.WordBrowserFragment;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;


public class MainActivity extends MvpAppCompatActivity implements MainView {
    private WordBrowserFragment wordBrowserFragment;
    private TranslatorFragment translatorFragment;

    @InjectPresenter
    MainPresenter presenter;

    @BindView(R.id.bottom_nav_bar) BottomNavigationViewEx bottomNavBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initViews();

        if (savedInstanceState == null) {
            bottomNavBar.setSelectedItemId(R.id.word_browser_menu_item);
        }
    }

    public void initViews() {
        bottomNavBar.enableAnimation(false);
        bottomNavBar.enableShiftingMode(false);
        bottomNavBar.enableItemShiftingMode(false);
        bottomNavBar.setTextVisibility(false);

        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                presenter.bottomNavigationClick(bottomNavBar.getMenuItemPosition(item));


                //Starting intent to send realm db via mail
                if (bottomNavBar.getMenuItemPosition(item) == 3) {
                    exportDatabase();
                }

                return true;

            }
        });
    }


    @Override
    public void replaceFragment(int index, int previousIndex) {
        Fragment fragment;
        FragmentTransaction fts = getFragmentManager().beginTransaction();

        if (index == 0) {
            fragment = new WordBrowserFragment();
        } else { //if index = 2
            fragment = new TranslatorFragment();
        }

        // Choose animations
        if (index == previousIndex) {
            fts.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        } /*else if (index == 1) {
            if (previousIndex == 0) {
                fts.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);
            } else {
                fts.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
            }
        } else if (index == 2) {
            fts.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);
        }*/

        // Execute transaction
        fts.replace(R.id.fragment_container, fragment).commit();
    }

    public void exportDatabase() {

        // init realm
        Realm realm = Realm.getDefaultInstance();

        File exportRealmFile = null;
        // get or create an "export.realm" file
        exportRealmFile = new File(this.getExternalCacheDir(), "export.realm");

        // if "export.realm" already exists, delete
        exportRealmFile.delete();

        // copy current realm to "export.realm"
        realm.writeCopyTo(exportRealmFile);


        realm.close();

        // init email intent and add export.realm as attachment
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, "melnikov.ws@gmail.com");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My Database");
        intent.putExtra(Intent.EXTRA_TEXT, "realm database file");
        Uri u = Uri.fromFile(exportRealmFile);
        intent.putExtra(Intent.EXTRA_STREAM, u);

        // start email intent
        startActivity(Intent.createChooser(intent, "title"));
    }

}
