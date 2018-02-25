package com.example.alexmelnikov.vocabra.ui;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.MenuItem;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.example.alexmelnikov.vocabra.R;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


import butterknife.BindView;
import butterknife.ButterKnife;


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
                FragmentManager fragMan = getFragmentManager();
                switch (item.getItemId()) {
                    case R.id.word_browser_menu_item:
                        wordBrowserFragment = new WordBrowserFragment();
                        fragMan.beginTransaction()
                                .replace(R.id.fragment_container, wordBrowserFragment)
                                .commit();
                        break;
                    case R.id.translator_menu_item:
                        translatorFragment = new TranslatorFragment();
                        fragMan.beginTransaction()
                                .replace(R.id.fragment_container, translatorFragment)
                                .commit();
                        break;
                }
                return true;
            }
        });
    }

}
